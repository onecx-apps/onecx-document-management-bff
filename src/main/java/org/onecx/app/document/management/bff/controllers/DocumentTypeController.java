
package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;

import gen.org.tkit.onecx.document_management.client.api.DocumentTypeControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.DocumentTypeCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.client.model.DocumentTypeDTO;

public class DocumentTypeController implements DocumentTypeControllerV1Api {

    @Inject

    @RestClient
    DocumentTypeControllerV1Api documentTypeControllerV1Api;

    @Inject
    DocumentMapper mapper;

    @Override
    public DocumentTypeDTO createDocumentType(DocumentTypeCreateUpdateDTO documentTypeCreateUpdateDTO) {
        return documentTypeControllerV1Api.createDocumentType(documentTypeCreateUpdateDTO);
    }

    @Override
    public Response deleteDocumentTypeById(String id) {
        return documentTypeControllerV1Api.deleteDocumentTypeById(id);
    }

    @Override
    public List<DocumentTypeDTO> getAllTypesOfDocument() {
        return documentTypeControllerV1Api.getAllTypesOfDocument();
    }

    @Override
    public DocumentTypeDTO getDocumentTypeById(String id) {
        return documentTypeControllerV1Api.getDocumentTypeById(id);
    }

    @Override
    public DocumentTypeDTO updateDocumentTypeById(String id, DocumentTypeCreateUpdateDTO documentTypeCreateUpdateDTO) {
        return documentTypeControllerV1Api.updateDocumentTypeById(id, documentTypeCreateUpdateDTO);
    }
}
