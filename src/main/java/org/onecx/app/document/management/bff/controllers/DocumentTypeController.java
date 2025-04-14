
package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;

import gen.org.tkit.onecx.document_management.client.api.DocumentTypeControllerV1Api;
import gen.org.tkit.onecx.document_management.rs.internal.DocumentTypeControllerV1ApiService;
import gen.org.tkit.onecx.document_management.rs.internal.model.DocumentTypeCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.rs.internal.model.DocumentTypeDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DocumentTypeController implements DocumentTypeControllerV1ApiService {
    @Inject
    @RestClient
    DocumentTypeControllerV1Api documentTypeControllerV1Api;

    @Inject
    DocumentMapper mapper;

    @Override
    public Response createDocumentType(DocumentTypeCreateUpdateDTO documentTypeCreateUpdateDTO) {
        try (Response response = documentTypeControllerV1Api.createDocumentType(mapper.map(documentTypeCreateUpdateDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.mapDocumentType(response.readEntity(DocumentTypeDTO.class)))
                    .build();
        }
    }

    @Override
    public Response deleteDocumentTypeById(String id) {
        try (Response response = documentTypeControllerV1Api.deleteDocumentTypeById(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAllTypesOfDocument() {
        try (Response response = documentTypeControllerV1Api.getAllTypesOfDocument()) {
            List<DocumentTypeDTO> documentTypeList = response.readEntity(new GenericType<List<DocumentTypeDTO>>() {
            });
            return Response.status(response.getStatus())
                    .entity(mapper.mapType(documentTypeList))
                    .build();
        }
    }

    @Override
    public Response getDocumentTypeById(String id) {
        try (Response response = documentTypeControllerV1Api.getDocumentTypeById(id)) {
            return Response.status(response.getStatus())
                    .entity(mapper.mapDocumentType(response.readEntity(DocumentTypeDTO.class)))
                    .build();
        }
    }

    @Override
    public Response updateDocumentTypeById(String id, DocumentTypeCreateUpdateDTO documentTypeCreateUpdateDTO) {
        try (Response response = documentTypeControllerV1Api.updateDocumentTypeById(id,
                mapper.map(documentTypeCreateUpdateDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.mapDocumentType(response.readEntity(DocumentTypeDTO.class)))
                    .build();
        }
    }
}
