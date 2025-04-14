
package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;

import gen.org.tkit.onecx.document_management.client.api.DocumentSpecificationControllerV1Api;
import gen.org.tkit.onecx.document_management.rs.internal.DocumentSpecificationControllerV1ApiService;
import gen.org.tkit.onecx.document_management.rs.internal.model.DocumentSpecificationCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.rs.internal.model.DocumentSpecificationDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DocumentSpecificationController implements DocumentSpecificationControllerV1ApiService {
    @Inject
    @RestClient
    DocumentSpecificationControllerV1Api documentSpecificationControllerV1Api;

    @Inject
    DocumentMapper mapper;

    @Override
    public Response createDocumentSpecification(DocumentSpecificationCreateUpdateDTO documentSpecificationCreateUpdateDTO) {
        try (Response response = documentSpecificationControllerV1Api
                .createDocumentSpecification(mapper.map(documentSpecificationCreateUpdateDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(DocumentSpecificationDTO.class)))
                    .build();
        }
    }

    @Override
    public Response deleteDocumentSpecificationById(String id) {
        try (Response response = documentSpecificationControllerV1Api.deleteDocumentSpecificationById(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAllDocumentSpecifications() {
        try (Response response = documentSpecificationControllerV1Api.getAllDocumentSpecifications()) {
            List<DocumentSpecificationDTO> documentSpecificationList = response
                    .readEntity(new GenericType<List<DocumentSpecificationDTO>>() {
                    });
            return Response.status(response.getStatus())
                    .entity(mapper.mapSpecification(documentSpecificationList))
                    .build();
        }
    }

    @Override
    public Response getDocumentSpecificationById(String id) {
        try (Response response = documentSpecificationControllerV1Api.getDocumentSpecificationById(id)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(DocumentSpecificationDTO.class)))
                    .build();
        }
    }

    @Override
    public Response updateDocumentSpecificationById(String id,
            DocumentSpecificationCreateUpdateDTO documentSpecificationCreateUpdateDTO) {
        try (Response response = documentSpecificationControllerV1Api.updateDocumentSpecificationById(id,
                mapper.map(documentSpecificationCreateUpdateDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(DocumentSpecificationDTO.class)))
                    .build();
        }
    }
}
