package org.onecx.app.document.management.bff.service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.onecx.app.document.management.bff.config.StorageIntegrationConfig;
import org.onecx.app.document.management.bff.exception.ErrorCode;
import org.onecx.app.document.management.bff.exception.RestException;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;
import org.onecx.app.document.management.bff.model.MetadataResult;
import org.onecx.app.document.management.bff.model.UploadUrlResult;

import gen.org.tkit.onecx.document_management.client.api.AttachmentControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.Attachment;
import gen.org.tkit.onecx.document_management.client.model.AttachmentStorageAuditRequest;
import gen.org.tkit.onecx.document_management.client.model.DocumentDetail;
import gen.org.tkit.onecx.document_management.rs.internal.model.UpdateFileMetadataRequestDTO;
import gen.org.tkit.onecx.document_management.rs.internal.model.UploadAttachmentPresignedUrlRequestDTO;
import gen.org.tkit.onecx.filestorage.client.api.FileStorageApi;
import gen.org.tkit.onecx.filestorage.client.model.*;

@ApplicationScoped
public class AttachmentService {

    @Inject
    @RestClient
    FileStorageApi fileStorageApi;

    @Inject
    @RestClient
    AttachmentControllerV1Api attachmentClient;

    @Inject
    DocumentMapper documentMapper;

    @Inject
    StorageIntegrationConfig storageConfig;

    public List<UploadUrlResult> getUploadPresignedUrls(final DocumentDetail documentDetail,
            List<UploadAttachmentPresignedUrlRequestDTO> request) {
        var attachmentsToProcess = resolveAttachmentsToProcess(documentDetail, request);
        var results = new ArrayList<UploadUrlResult>();
        attachmentsToProcess
                .forEach(attachment -> results.add(processAttachment(documentDetail, attachment, request)));
        return results;
    }

    public PresignedUrlResponse getFilePresignedUrl(final String attachmentId) {
        final var attachment = getAttachment(attachmentId);
        final var downloadRequest = getPresignedUrlRequest(attachment);
        return getPresignedDownloadUrl(downloadRequest);
    }

    public Response updateAttachmentsMetadata(final DocumentDetail documentDetail,
            final List<UpdateFileMetadataRequestDTO> metadataRequests) {
        final var attachmentIds = metadataRequests.stream().map(UpdateFileMetadataRequestDTO::getAttachmentId)
                .collect(Collectors.toSet());
        final var attachmentNameIdMap = documentDetail.getAttachments().stream()
                .filter(att -> attachmentIds.contains(att.getId()))
                .collect(Collectors.toMap(att -> getUploadFileName(att.getId(), att.getFileName()), Attachment::getId));

        final var metadataStorageRequests = attachmentNameIdMap.keySet().stream().map(this::getMetadataRequest).toList();
        Response storageResponse;
        try {
            storageResponse = fileStorageApi.getMetadataForFiles(metadataStorageRequests);
        } catch (WebApplicationException e) {
            throw new RestException(Response.Status.BAD_REQUEST,
                    "Metadata could not be uploaded", ErrorCode.METADATA_ERROR);
        }
        final var metadataResults = processMetadataResult(storageResponse, attachmentNameIdMap);
        final var updateMetadataRequests = metadataResults.stream().map(documentMapper::mapToMetadataUpload).toList();
        return attachmentClient.uploadAttachmentsMetadata(updateMetadataRequests);
    }

    public Response createAttachmentsAuditLogs(final String documentId,
            final List<UpdateFileMetadataRequestDTO> updateFileMetadataRequestDTO) {
        final var requests = updateFileMetadataRequestDTO.stream().map(req -> {
            final var auditDto = new AttachmentStorageAuditRequest();
            auditDto.setAttachmentId(req.getAttachmentId());
            auditDto.setDocumentId(documentId);
            return auditDto;
        }).toList();
        try {
            return attachmentClient.createStorageAuditsForAttachments(requests);
        } catch (ClientWebApplicationException e) {
            var response = e.getResponse();
            throw new RestException(Response.Status.fromStatusCode(response.getStatus()), "Error on creating audit logs",
                    ErrorCode.METADATA_ERROR);
        }
    }

    public void deleteDocumentAttachmentFiles(final List<Attachment> attachments) {
        var deleteRequests = attachments.stream().map(this::getFileDeleteRequest).toList();
        deleteRequests.forEach(request -> {
            try {
                fileStorageApi.deleteFile(request);
            } catch (Exception e) {
                var message = String.format("Attachment %s file could not be deleted", request.getFileName());
                throw new RestException(Response.Status.BAD_REQUEST, message, ErrorCode.FILE_DELETE_ERROR);
            }
        });
    }

    private UploadUrlResult processAttachment(final DocumentDetail documentDetail, final Attachment attachment,
            final List<UploadAttachmentPresignedUrlRequestDTO> requestedAttachments) {
        final var matchedAttachmentOpt = requestedAttachments.stream()
                .filter(requestedAttachment -> attachment.getFileName().equals(requestedAttachment.getFileName()))
                .findFirst();
        if (matchedAttachmentOpt.isEmpty()) {
            return getFailedUploadResult(documentDetail.getId(), attachment.getId());
        }
        final var matchedAttachment = matchedAttachmentOpt.get();
        final var request = getPresignedUrlRequest(matchedAttachment);
        try {
            var urlBody = getPresignedUploadUrl(request);
            return getSuccessfulUploadResult(documentDetail.getId(), attachment.getId(), urlBody.getUrl(),
                    urlBody.getExpiration());
        } catch (Exception e) {
            return getFailedUploadResult(documentDetail.getId(), attachment.getId());
        }
    }

    private String getUploadFileName(final String attachmentId, final String fileName) {
        return String.format("%s%s%s", attachmentId, storageConfig.fileNameSeparator(), fileName);
    }

    private Set<Attachment> resolveAttachmentsToProcess(DocumentDetail document,
            List<UploadAttachmentPresignedUrlRequestDTO> requests) {
        Set<Attachment> attachmentSet = new HashSet<>();
        requests.forEach(uploadRequest -> document.getAttachments().stream()
                .filter(attachment -> uploadRequest.getAttachmentId().equals(attachment.getId()))
                .findFirst()
                .ifPresent(attachmentSet::add));
        return attachmentSet;
    }

    private UploadUrlResult getFailedUploadResult(final String documentId, final String attachmentId) {
        return UploadUrlResult.builder()
                .documentId(documentId)
                .attachmentId(attachmentId)
                .operationStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .build();
    }

    private UploadUrlResult getSuccessfulUploadResult(final String documentId, final String attachmentId,
            final String url, final OffsetDateTime expiring) {
        return UploadUrlResult.builder()
                .documentId(documentId)
                .attachmentId(attachmentId)
                .operationStatusCode(Response.Status.OK.getStatusCode())
                .url(url)
                .expiration(expiring).build();

    }

    private PresignedUrlRequest getPresignedUrlRequest(final UploadAttachmentPresignedUrlRequestDTO uploadRequest) {
        var fileName = getUploadFileName(uploadRequest.getAttachmentId(), uploadRequest.getFileName());
        return getPresignedUrlRequest(fileName);
    }

    private PresignedUrlRequest getPresignedUrlRequest(final Attachment attachment) {
        final var fileName = getUploadFileName(attachment.getId(), attachment.getFileName());
        return getPresignedUrlRequest(fileName);
    }

    private PresignedUrlRequest getPresignedUrlRequest(final String fileName) {
        final var request = new PresignedUrlRequest();
        request.setApplicationId(storageConfig.applicationId());
        request.setProductName(storageConfig.productName());
        request.setFileName(fileName);
        return request;
    }

    private Attachment getAttachment(final String attachmentId) {
        Response attResponse;
        try {
            attResponse = attachmentClient.getAttachmentDetails(attachmentId);
        } catch (ClientWebApplicationException e) {
            var exceptionResponse = e.getResponse();
            if (exceptionResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                var msg = String.format("Attachment %s not found", attachmentId);
                throw new RestException(Response.Status.NOT_FOUND, msg, ErrorCode.ATTACHMENT_NOT_FOUND);
            }
            var msg = String.format("Attachment %s could not be retrieved", attachmentId);
            throw new RestException(Response.Status.BAD_REQUEST, msg, ErrorCode.ATTACHMENT_ERROR);
        }
        return attResponse.readEntity(Attachment.class);
    }

    private PresignedUrlResponse getPresignedDownloadUrl(final PresignedUrlRequest downloadRequest) {
        Response response;
        try {
            response = fileStorageApi.getPresignedDownloadUrl(downloadRequest);
        } catch (ClientWebApplicationException e) {
            var msg = "Could not receive file download url";
            throw new RestException(Response.Status.BAD_REQUEST, msg, ErrorCode.PRESIGNED_URL_ERROR);
        }
        return handlePresignedUrlResponse(response);
    }

    private PresignedUrlResponse getPresignedUploadUrl(final PresignedUrlRequest updateRequest) {
        Response response;
        try {
            response = fileStorageApi.getPresignedUploadUrl(updateRequest);
        } catch (ClientWebApplicationException e) {
            response = e.getResponse();
        }
        return handlePresignedUrlResponse(response);
    }

    private PresignedUrlResponse handlePresignedUrlResponse(final Response response) {
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new RestException(Response.Status.BAD_REQUEST, "Error during receiving presigned url",
                    ErrorCode.PRESIGNED_URL_ERROR);
        }
        return response.readEntity(PresignedUrlResponse.class);
    }

    private FileMetadataRequest getMetadataRequest(final String fileName) {
        final var request = new FileMetadataRequest();
        request.setFileName(fileName);
        request.setProductName(storageConfig.productName());
        request.setApplicationId(storageConfig.applicationId());
        return request;
    }

    private List<MetadataResult> processMetadataResult(final Response response, final Map<String, String> fileNameIdMap) {
        final var results = response.readEntity(new GenericType<List<FileMetadataResponse>>() {
        });
        return results.stream().map(result -> {
            final var receivedFileName = result.getFileName();
            final var attId = fileNameIdMap.get(receivedFileName);
            return new MetadataResult(attId, result);
        }).toList();
    }

    private FileDeleteRequest getFileDeleteRequest(final Attachment attachment) {
        var finalFileName = getUploadFileName(attachment.getId(), attachment.getFileName());
        var deleteRequest = new FileDeleteRequest();
        deleteRequest.setFileName(finalFileName);
        deleteRequest.setApplicationId(storageConfig.applicationId());
        deleteRequest.setProductName(storageConfig.productName());
        return deleteRequest;
    }
}
