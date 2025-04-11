package org.onecx.app.document.management.bff.controllers;

import java.io.File;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataInput;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gen.org.tkit.onecx.document_management.client.api.DocumentControllerV1Api;
import gen.org.tkit.onecx.document_management.rs.internal.DocumentControllerV1ApiService;
import gen.org.tkit.onecx.document_management.rs.internal.model.*;

@ApplicationScoped
public class DocumentController implements DocumentControllerV1ApiService {
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    @Inject
    @RestClient
    DocumentControllerV1Api documentControllerV1Api;

    @Inject
    DocumentMapper mapper;

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
        try (Response response = documentControllerV1Api.deleteDocumentById(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response deleteFilesInBulk(List<String> requestBody) {
        try (Response response = documentControllerV1Api.deleteFilesInBulk(requestBody)) {
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
    public Response getAllDocumentAttachmentsAsZip(String documentId, String clientTimezone) {
        try (Response response = documentControllerV1Api.getAllDocumentAttachmentsAsZip(documentId, clientTimezone)) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(File.class))
                    .build();
        }
    }

    @Override
    public Response getDocumentByCriteria(String channelName, String createdBy, String endDate, String id, String name,
            String objectReferenceId, String objectReferenceType, Integer page, Integer size, String startDate,
            List<LifeCycleStateDTO> state, List<String> typeId) {
        try (Response response = documentControllerV1Api.getDocumentByCriteria(channelName, createdBy, endDate, id, name,
                objectReferenceId, objectReferenceType, page,
                size, startDate, mapper.mapLifeCycle(state), typeId)) {
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
        try (Response response = documentControllerV1Api.getFile(attachmentId)) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(File.class))
                    .build();
        }
    }

    @Override
    public Response showAllDocumentsByCriteria(String channelName, String createdBy, String endDate, String id, String name,
            String objectReferenceId, String objectReferenceType, Integer page, Integer size, String startDate,
            List<LifeCycleStateDTO> state, List<String> typeId) {
        try (Response response = documentControllerV1Api.showAllDocumentsByCriteria(channelName, createdBy, endDate, id, name,
                objectReferenceId, objectReferenceType,
                page, size, startDate, mapper.mapLifeCycle(state), typeId)) {
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
    public Response uploadAllFiles(String documentId, MultipartFormDataInput files) {
        DocumentControllerV1Api.UploadAllFilesMultipartForm multipart = new DocumentControllerV1Api.UploadAllFilesMultipartForm();
        multipart.files = files;

        try (Response response = documentControllerV1Api.uploadAllFiles(multipart, documentId)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(DocumentResponseDTO.class)))
                    .build();
        }
    }
}
