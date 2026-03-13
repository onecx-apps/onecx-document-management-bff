package org.onecx.app.document.management.bff.service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.config.StorageIntegrationConfig;
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
import gen.org.tkit.onecx.filestorage.client.model.FileMetadataRequest;
import gen.org.tkit.onecx.filestorage.client.model.FileMetadataResponse;
import gen.org.tkit.onecx.filestorage.client.model.PresignedUrlRequest;
import gen.org.tkit.onecx.filestorage.client.model.PresignedUrlResponse;

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
        final var storageResponse = fileStorageApi.getMetadataForFiles(metadataStorageRequests);
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
        return attachmentClient.createStorageAuditsForAttachments(requests);
    }

    private UploadUrlResult processAttachment(final DocumentDetail documentDetail, final Attachment attachment,
            final List<UploadAttachmentPresignedUrlRequestDTO> requestedAttachments) {
        final var matchedAttachmentOpt = requestedAttachments.stream()
                .filter(requestedAttachment -> attachment.getFileName().equals(requestedAttachment.getFileName()))
                .findFirst();
        if (matchedAttachmentOpt.isEmpty()) {
            return getUploadResult(documentDetail.getId(), attachment.getId());
        }
        final var matchedAttachment = matchedAttachmentOpt.get();
        final var request = getPresignedUrlRequest(matchedAttachment);
        try {
            var urlBody = getPresignedUploadUrl(request);
            return getUploadResult(documentDetail.getId(), attachment.getId(), Response.Status.OK.getStatusCode(),
                    urlBody.getUrl(), urlBody.getExpiration());
        } catch (Exception e) {
            return getUploadResult(documentDetail.getId(), attachment.getId());
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

    private UploadUrlResult getUploadResult(final String documentId, final String attachmentId) {
        return getUploadResult(documentId, attachmentId, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                null, null);
    }

    private UploadUrlResult getUploadResult(final String documentId, final String attachmentId, final Integer code,
            final String url, final OffsetDateTime expiring) {
        return new UploadUrlResult(documentId, attachmentId, code, url, expiring);
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
        final var attResponse = attachmentClient.getAttachmentDetails(attachmentId);
        if (attResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            throw new NotFoundException("Attachment not found");
        }
        if (attResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new InternalServerErrorException();
        }
        return attResponse.readEntity(Attachment.class);
    }

    private PresignedUrlResponse getPresignedDownloadUrl(final PresignedUrlRequest downloadRequest) {
        var response = fileStorageApi.getPresignedDownloadUrl(downloadRequest);
        return handlePresignedUrlResponse(response);
    }

    private PresignedUrlResponse getPresignedUploadUrl(final PresignedUrlRequest updateRequest) {
        var response = fileStorageApi.getPresignedUploadUrl(updateRequest);
        return handlePresignedUrlResponse(response);
    }

    private PresignedUrlResponse handlePresignedUrlResponse(final Response response) {
        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            throw new BadRequestException("File could not be downloaded");
        }
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new InternalServerErrorException();
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
        final var results = handleMetadataResponse(response);
        return results.stream().map(result -> {
            final var receivedFileName = result.getFileName();
            final var attId = fileNameIdMap.get(receivedFileName);
            return new MetadataResult(attId, result);
        }).toList();
    }

    private List<FileMetadataResponse> handleMetadataResponse(final Response response) {
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new InternalServerErrorException();
        }
        return response.readEntity(new GenericType<>() {
        });
    }
}
