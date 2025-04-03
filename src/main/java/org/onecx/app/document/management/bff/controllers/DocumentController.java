package org.onecx.app.document.management.bff.controllers;

import java.io.File;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gen.org.tkit.onecx.document_management.client.api.DocumentControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.*;

@ApplicationScoped
public class DocumentController implements DocumentControllerV1Api {
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    @Inject
    @RestClient
    DocumentControllerV1Api documentControllerV1Api;

    @Override
    public List<DocumentDetailDTO> bulkUpdateDocument(List<DocumentCreateUpdateDTO> documentCreateUpdateDTO) {
        try {
            return documentControllerV1Api.bulkUpdateDocument(documentCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while updating the documents", e);
            throw new RuntimeException("Error occurred while updating the documents", e);
        }
    }

    @Override
    public DocumentDetailDTO createDocument(DocumentCreateUpdateDTO documentCreateUpdateDTO) {
        try {
            return documentControllerV1Api.createDocument(documentCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while creating document", e);
            throw new RuntimeException("Error occurred while creating document", e);
        }
    }

    @Override
    public Response deleteBulkDocuments(List<String> requestBody) {
        try {
            return documentControllerV1Api.deleteBulkDocuments(requestBody);
        } catch (Exception e) {
            log.error("Error occurred while deleting bulk documents", e);
            throw new RuntimeException("Error occurred while deleting bulk documents", e);
        }
    }

    @Override
    public Response deleteDocumentById(String id) {
        try {
            return documentControllerV1Api.deleteDocumentById(id);
        } catch (Exception e) {
            log.error("Error occurred while deleting document by id", e);
            throw new RuntimeException("Error occurred while deleting document by id", e);
        }
    }

    @Override
    public Response deleteFilesInBulk(List<String> requestBody) {
        try {
            return documentControllerV1Api.deleteFilesInBulk(requestBody);
        } catch (Exception e) {
            log.error("Error occurred while deleting files in bulk", e);
            throw new RuntimeException("Error occurred while deleting files in bulk", e);
        }
    }

    @Override
    public List<ChannelDTO> getAllChannels() {
        try {
            return documentControllerV1Api.getAllChannels();
        } catch (Exception e) {
            log.error("Error occurred while fetching all channels", e);
            throw new RuntimeException("Error occurred while fetching all channels", e);
        }
    }

    @Override
    public File getAllDocumentAttachmentsAsZip(String documentId, String clientTimezone) {
        try {
            return documentControllerV1Api.getAllDocumentAttachmentsAsZip(documentId, clientTimezone);
        } catch (Exception e) {
            log.error("Error occurred while fetching document attachments as zip", e);
            throw new RuntimeException("Error occurred while fetching document attachments as zip", e);
        }
    }

    @Override
    public PageResultDTO getDocumentByCriteria(String channelName, String createdBy, String endDate, String id, String name,
            String objectReferenceId, String objectReferenceType, Integer page, Integer size, String startDate,
            List<LifeCycleState> state, List<String> typeId) {
        try {
            return documentControllerV1Api.getDocumentByCriteria(channelName, createdBy, endDate, id, name, objectReferenceId,
                    objectReferenceType, page, size, startDate, state, typeId);
        } catch (Exception e) {
            log.error("Error occurred while fetching documents by criteria", e);
            throw new RuntimeException("Error occurred while fetching documents by criteria", e);
        }
    }

    @Override
    public DocumentDetailDTO getDocumentById(String id) {
        try {
            return documentControllerV1Api.getDocumentById(id);
        } catch (Exception e) {
            log.error("Error occurred while fetching document by id", e);
            throw new RuntimeException("Error occurred while fetching document by id", e);
        }
    }

    @Override
    public List<StorageUploadAuditDTO> getFailedAttachmentData(String id) {
        try {
            return documentControllerV1Api.getFailedAttachmentData(id);
        } catch (Exception e) {
            log.error("Error occurred while fetching failed attachment data", e);
            throw new RuntimeException("Error occurred while fetching failed attachment data", e);
        }
    }

    @Override
    public File getFile(String attachmentId) {
        try {
            return documentControllerV1Api.getFile(attachmentId);
        } catch (Exception e) {
            log.error("Error occurred while fetching file", e);
            throw new RuntimeException("Error occurred while fetching file", e);
        }
    }

    @Override
    public List<DocumentDetailDTO> showAllDocumentsByCriteria(String channelName, String createdBy, String endDate, String id,
            String name, String objectReferenceId, String objectReferenceType, Integer page, Integer size, String startDate,
            List<LifeCycleState> state, List<String> typeId) {
        try {
            return documentControllerV1Api.showAllDocumentsByCriteria(channelName, createdBy, endDate, id, name,
                    objectReferenceId,
                    objectReferenceType,
                    page, size, startDate, state, typeId);
        } catch (Exception e) {
            log.error("Error occurred while fetching all documents by criteria", e);
            throw new RuntimeException("Error occurred while fetching all documents by criteria", e);
        }
    }

    @Override
    public DocumentDetailDTO updateDocument(String id, DocumentCreateUpdateDTO documentCreateUpdateDTO) {
        try {
            return documentControllerV1Api.updateDocument(id, documentCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while updating document", e);
            throw new RuntimeException("Error occurred while updating document", e);
        }
    }

    @Override
    public DocumentResponseDTO uploadAllFiles(UploadAllFilesMultipartForm multipartForm, String documentId) {
        try {
            return documentControllerV1Api.uploadAllFiles(multipartForm, documentId);
        } catch (Exception e) {
            log.error("Error occurred while uploading all files", e);
            throw new RuntimeException("Error occurred while uploading all files", e);
        }
    }
}
