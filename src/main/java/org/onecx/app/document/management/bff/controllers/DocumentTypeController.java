
package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.org.tkit.onecx.document_management.client.api.DocumentTypeControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.DocumentTypeCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.client.model.DocumentTypeDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DocumentTypeController implements DocumentTypeControllerV1Api {
    @Inject
    @RestClient
    DocumentTypeControllerV1Api documentTypeControllerV1Api;

    @Override
    public DocumentTypeDTO createDocumentType(DocumentTypeCreateUpdateDTO documentTypeCreateUpdateDTO) {
        try {
            return documentTypeControllerV1Api.createDocumentType(documentTypeCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while creating document type", e);
            throw new RuntimeException("Error occurred while creating document type", e);
        }
    }

    @Override
    public Response deleteDocumentTypeById(String id) {
        try {
            return documentTypeControllerV1Api.deleteDocumentTypeById(id);
        } catch (Exception e) {
            log.error("Error occurred while deleting document type", e);
            throw new RuntimeException("Error occurred while deleting document type", e);
        }
    }

    @Override
    public List<DocumentTypeDTO> getAllTypesOfDocument() {
        try {
            return documentTypeControllerV1Api.getAllTypesOfDocument();
        } catch (Exception e) {
            log.error("Error occurred while fetching all document types", e);
            throw new RuntimeException("Error occurred while fetching all document types", e);
        }
    }

    @Override
    public DocumentTypeDTO getDocumentTypeById(String id) {
        try {
            return documentTypeControllerV1Api.getDocumentTypeById(id);
        } catch (Exception e) {
            log.error("Error occurred while fetching document type", e);
            throw new RuntimeException("Error occurred while fetching document type", e);
        }
    }

    @Override
    public DocumentTypeDTO updateDocumentTypeById(String id, DocumentTypeCreateUpdateDTO documentTypeCreateUpdateDTO) {
        try {
            return documentTypeControllerV1Api.updateDocumentTypeById(id, documentTypeCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while updating document type", e);
            throw new RuntimeException("Error occurred while updating document type", e);
        }
    }
}
