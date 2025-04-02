package org.onecx.app.document.management.bff.controllers;

import java.io.File;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gen.org.tkit.onecx.document_management.client.api.DocumentControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.*;

@ApplicationScoped
//@LogService
public class DocumentController implements DocumentControllerV1Api {
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    @Inject
    @RestClient
    DocumentControllerV1Api documentControllerV1Api;
    @Inject
    DocumentMapper mapper;

    @Override
    public List<DocumentDetailDTO> bulkUpdateDocument(List<DocumentCreateUpdateDTO> documentCreateUpdateDTO) {
        return documentControllerV1Api.bulkUpdateDocument(documentCreateUpdateDTO);
    }

    @Override
    public DocumentDetailDTO createDocument(DocumentCreateUpdateDTO documentCreateUpdateDTO) {
        log.info("inside document controller - create document");
        return documentControllerV1Api.createDocument(documentCreateUpdateDTO);
    }

    @Override
    public Response deleteBulkDocuments(List<String> requestBody) {
        return documentControllerV1Api.deleteBulkDocuments(requestBody);
    }

    @Override
    public Response deleteDocumentById(String id) {
        return documentControllerV1Api.deleteDocumentById(id);
    }

    @Override
    public Response deleteFilesInBulk(List<String> requestBody) {
        return documentControllerV1Api.deleteFilesInBulk(requestBody);
    }

    @Override
    public List<ChannelDTO> getAllChannels() {
        return documentControllerV1Api.getAllChannels();
    }

    @Override
    public File getAllDocumentAttachmentsAsZip(String documentId, String clientTimezone) {
        return documentControllerV1Api.getAllDocumentAttachmentsAsZip(documentId, clientTimezone);
    }

    @Override
    public PageResultDTO getDocumentByCriteria(String channelName, String createdBy, String endDate, String id, String name,
            String objectReferenceId, String objectReferenceType, Integer page, Integer size, String startDate,
            List<LifeCycleState> state, List<String> typeId) {
        return documentControllerV1Api.getDocumentByCriteria(channelName, createdBy, endDate, id, name, objectReferenceId,
                objectReferenceType, page, size, startDate, state, typeId);
    }

    @Override
    public DocumentDetailDTO getDocumentById(String id) {
        return documentControllerV1Api.getDocumentById(id);
    }

    @Override
    public List<StorageUploadAuditDTO> getFailedAttachmentData(String id) {
        return documentControllerV1Api.getFailedAttachmentData(id);
    }

    @Override
    public File getFile(String attachmentId) {
        return documentControllerV1Api.getFile(attachmentId);
    }

    @Override
    public List<DocumentDetailDTO> showAllDocumentsByCriteria(String channelName, String createdBy, String endDate, String id,
            String name, String objectReferenceId, String objectReferenceType, Integer page, Integer size, String startDate,
            List<LifeCycleState> state, List<String> typeId) {
        return documentControllerV1Api.showAllDocumentsByCriteria(channelName, createdBy, endDate, id, name, objectReferenceId,
                objectReferenceType,
                page, size, startDate, state, typeId);
    }

    @Override
    public DocumentDetailDTO updateDocument(String id, DocumentCreateUpdateDTO documentCreateUpdateDTO) {
        return documentControllerV1Api.updateDocument(id, documentCreateUpdateDTO);
    }

    @Override
    public DocumentResponseDTO uploadAllFiles(UploadAllFilesMultipartForm multipartForm, String documentId) {
        return documentControllerV1Api.uploadAllFiles(multipartForm, documentId);
    }
}
