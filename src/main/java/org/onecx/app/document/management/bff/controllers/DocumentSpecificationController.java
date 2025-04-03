
package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;

import gen.org.tkit.onecx.document_management.client.api.DocumentSpecificationControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.*;
//import gen.org.tkit.onecx.document_management.rs.internal.model.DocumentSpecificationCreateUpdateDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DocumentSpecificationController implements DocumentSpecificationControllerV1Api {
    @Inject
    @RestClient
    DocumentSpecificationControllerV1Api documentSpecificationControllerV1Api;

    @Inject
    DocumentMapper mapper;

    @Override
    public DocumentSpecificationDTO createDocumentSpecification(
            DocumentSpecificationCreateUpdateDTO documentSpecificationCreateUpdateDTO) {
        try {
            return documentSpecificationControllerV1Api.createDocumentSpecification(documentSpecificationCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while creating document specification", e);
            throw new RuntimeException("Error occurred while creating document specification", e);
        }
    }

    @Override
    public Response deleteDocumentSpecificationById(String id) {
        try {
            return documentSpecificationControllerV1Api.deleteDocumentSpecificationById(id);
        } catch (Exception e) {
            log.error("Error occurred while deleting document specification", e);
            throw new RuntimeException("Error occurred while deleting document specification", e);
        }
    }

    @Override
    public List<DocumentSpecificationDTO> getAllDocumentSpecifications() {
        try {
            return documentSpecificationControllerV1Api.getAllDocumentSpecifications();
        } catch (Exception e) {
            log.error("Error occurred while fetching all document specifications", e);
            throw new RuntimeException("Error occurred while fetching all document specifications", e);
        }
    }

    @Override
    public DocumentSpecificationDTO getDocumentSpecificationById(String id) {
        try {
            return documentSpecificationControllerV1Api.getDocumentSpecificationById(id);
        } catch (Exception e) {
            log.error("Error occurred while fetching document specification", e);
            throw new RuntimeException("Error occurred while fetching document specification", e);
        }
    }

    @Override
    public DocumentSpecificationDTO updateDocumentSpecificationById(String id,
            DocumentSpecificationCreateUpdateDTO documentSpecificationCreateUpdateDTO) {
        try {
            return documentSpecificationControllerV1Api.updateDocumentSpecificationById(id,
                    documentSpecificationCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while updating document specification", e);
            throw new RuntimeException("Error occurred while updating document specification", e);
        }
    }

}
