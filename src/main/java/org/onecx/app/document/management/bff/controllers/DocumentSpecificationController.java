
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
import gen.org.tkit.onecx.document_management.rs.internal.model.DocumentSpecification;
import gen.org.tkit.onecx.document_management.rs.internal.model.DocumentSpecificationCreateUpdate;
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
    public Response createDocumentSpecification(DocumentSpecificationCreateUpdate documentSpecificationCreateUpdate) {
        try (Response response = documentSpecificationControllerV1Api
                .createDocumentSpecification(mapper.map(documentSpecificationCreateUpdate))) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(DocumentSpecification.class))
                    .build();
        }
    }

    @Override
    public Response deleteDocumentSpecificationById(String id) {
        try (Response response = documentSpecificationControllerV1Api.deleteDocumentSpecificationById(id)) {
            return response;
        }
    }

    @Override
    public Response getAllDocumentSpecifications() {
        try (Response response = documentSpecificationControllerV1Api.getAllDocumentSpecifications()) {
            List<DocumentSpecification> documentSpecificationList = response
                    .readEntity(new GenericType<List<DocumentSpecification>>() {
                    });
            return Response.status(response.getStatus())
                    .entity(documentSpecificationList)
                    .build();
        }
    }

    @Override
    public Response getDocumentSpecificationById(String id) {
        try (Response response = documentSpecificationControllerV1Api.getDocumentSpecificationById(id)) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(DocumentSpecification.class))
                    .build();
        }
    }

    @Override
    public Response updateDocumentSpecificationById(String id,
            DocumentSpecificationCreateUpdate documentSpecificationCreateUpdate) {
        try (Response response = documentSpecificationControllerV1Api.updateDocumentSpecificationById(id,
                mapper.map(documentSpecificationCreateUpdate))) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(DocumentSpecification.class))
                    .build();
        }
    }
}
