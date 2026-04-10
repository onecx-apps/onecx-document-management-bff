package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.onecx.app.document.management.bff.exception.RestException;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;
import org.onecx.app.document.management.bff.mappers.ExceptionMapper;
import org.onecx.app.document.management.bff.service.AttachmentService;

import gen.org.tkit.onecx.document_management.client.api.DocumentControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.DocumentDetail;
import gen.org.tkit.onecx.document_management.rs.internal.DocumentControllerV1ApiService;
import gen.org.tkit.onecx.document_management.rs.internal.model.*;

@ApplicationScoped
public class DocumentController implements DocumentControllerV1ApiService {
    @Inject
    @RestClient
    DocumentControllerV1Api documentControllerV1Api;

    @Inject
    DocumentMapper mapper;

    @Inject
    AttachmentService fileService;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response bulkUpdateDocument(List<DocumentCreateUpdateDTO> documentCreateUpdateDTOS) {
        try (Response response = documentControllerV1Api.bulkUpdateDocument(mapper.map(documentCreateUpdateDTOS))) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(DocumentDetailDTO.class)))
                    .build();
        }
    }

    @Override
    public Response createDocument(DocumentCreateUpdateDTO documentCreateUpdateDTO) {
        try (Response response = documentControllerV1Api.createDocument(mapper.map(documentCreateUpdateDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(DocumentDetailDTO.class)))
                    .build();
        }
    }

    @Override
    public Response deleteBulkDocuments(List<String> requestBody) {
        try (Response response = documentControllerV1Api.deleteBulkDocuments(requestBody)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response deleteDocumentById(String id) {
        var docDetail = documentControllerV1Api.getDocumentById(id).readEntity(DocumentDetail.class);
        var attachments = docDetail.getAttachments();
        try (Response response = documentControllerV1Api.deleteDocumentById(id)) {
            fileService.deleteDocumentAttachmentFiles(attachments);
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAllChannels() {
        try (Response response = documentControllerV1Api.getAllChannels()) {
            return Response.status(response.getStatus())
                    .entity(mapper.mapChannel(response.readEntity(new GenericType<List<ChannelDTO>>() {
                    })))
                    .build();
        }
    }

    @Override
    public Response getDocumentByCriteria(DocumentSearchCriteriaDTO criteriaDTO) {
        var internalCriteria = mapper.mapToInternalCriteria(criteriaDTO);
        try (Response response = documentControllerV1Api.getDocumentByCriteria(internalCriteria)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(PageResultDTO.class)))
                    .build();
        }
    }

    @Override
    public Response getDocumentById(String id) {
        try (Response response = documentControllerV1Api.getDocumentById(id)) {
            DocumentDetailDTO detailDTO = response.readEntity(DocumentDetailDTO.class);
            return Response.status(response.getStatus())
                    .entity(mapper.map(detailDTO))
                    .build();
        }
    }

    @Override
    public Response getFailedAttachmentData(String id) {
        try (Response response = documentControllerV1Api.getFailedAttachmentData(id)) {
            return Response.status(response.getStatus())
                    .entity(mapper.mapAuditList(response
                            .readEntity(new GenericType<List<StorageUploadAuditDTO>>() {
                            })))
                    .build();
        }
    }

    @Override
    public Response getFile(String attachmentId) {
        var presignedUrl = fileService.getFilePresignedUrl(attachmentId);
        return Response
                .ok(mapper.mapPresignedUrl(presignedUrl))
                .build();
    }

    @Override
    public Response showAllDocumentsByCriteria(DocumentSearchCriteriaDTO criteriaDTO) {
        var internalCriteria = mapper.mapToInternalCriteria(criteriaDTO);
        try (Response response = documentControllerV1Api.showAllDocumentsByCriteria(internalCriteria)) {
            return Response.status(response.getStatus())
                    .entity(mapper.mapDetailList(response.readEntity(new GenericType<List<DocumentDetailDTO>>() {
                    })))
                    .build();
        }
    }

    @Override
    public Response updateDocument(String id, DocumentCreateUpdateDTO documentCreateUpdateDTO) {
        try (Response response = documentControllerV1Api.updateDocument(id, mapper.map(documentCreateUpdateDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(DocumentDetailDTO.class)))
                    .build();
        }
    }

    @Override
    public Response uploadAllFiles(String documentId, List<UploadAttachmentPresignedUrlRequestDTO> request) {
        final var documentDetail = documentControllerV1Api.getDocumentById(documentId)
                .readEntity(DocumentDetail.class);
        final var uploadResults = fileService.getUploadPresignedUrls(documentDetail, request);
        return Response.ok()
                .entity(mapper.mapUploadResponse(uploadResults))
                .build();
    }

    @Override
    public Response updateAttachmentsMetadata(String documentId,
            List<UpdateFileMetadataRequestDTO> updateFileMetadataRequestDTO) {
        final var documentDetail = documentControllerV1Api.getDocumentById(documentId)
                .readEntity(DocumentDetail.class);
        return fileService.updateAttachmentsMetadata(documentDetail, updateFileMetadataRequestDTO);
    }

    @Override
    public Response createFailedAttachmentsAuditLogs(String documentId,
            List<UpdateFileMetadataRequestDTO> updateFileMetadataRequestDTO) {
        return fileService.createAttachmentsAuditLogs(documentId, updateFileMetadataRequestDTO);
    }

    @ServerExceptionMapper(priority = 1)
    public Response handleRestException(RestException restException) {
        return Response.status(restException.getStatus())
                .entity(exceptionMapper.map(restException))
                .build();
    }

    @ServerExceptionMapper
    public Response handleClientWebApplicationException(WebApplicationException webApplicationException) {
        return webApplicationException.getResponse();
    }
}
