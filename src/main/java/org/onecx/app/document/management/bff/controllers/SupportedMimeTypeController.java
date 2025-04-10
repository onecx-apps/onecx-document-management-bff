package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;

import gen.org.tkit.onecx.document_management.client.api.SupportedMimeTypeControllerV1Api;
import gen.org.tkit.onecx.document_management.rs.internal.SupportedMimeTypeControllerV1ApiService;
import gen.org.tkit.onecx.document_management.rs.internal.model.SupportedMimeType;
import gen.org.tkit.onecx.document_management.rs.internal.model.SupportedMimeTypeCreateUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class SupportedMimeTypeController implements SupportedMimeTypeControllerV1ApiService {
    @Inject
    @RestClient
    SupportedMimeTypeControllerV1Api supportedMimeTypeControllerV1Api;

    @Inject
    DocumentMapper mapper;

    @Override
    public Response createSupportedMimeType(SupportedMimeTypeCreateUpdate supportedMimeTypeCreateUpdate) {
        try (Response response = supportedMimeTypeControllerV1Api
                .createSupportedMimeType(mapper.map(supportedMimeTypeCreateUpdate))) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(SupportedMimeType.class))
                    .build();
        }
    }

    @Override
    public Response deleteSupportedMimeTypeId(String id) {
        try (Response response = supportedMimeTypeControllerV1Api.deleteSupportedMimeTypeId(id)) {
            return response;
        }
    }

    @Override
    public Response getAllSupportedMimeTypes() {
        try (Response response = supportedMimeTypeControllerV1Api.getAllSupportedMimeTypes()) {
            List<SupportedMimeType> supportedMimeTypeList = response.readEntity(new GenericType<List<SupportedMimeType>>() {
            });
            return Response.status(response.getStatus())
                    .entity(supportedMimeTypeList)
                    .build();
        }
    }

    @Override
    public Response getSupportedMimeTypeById(String id) {
        try (Response response = supportedMimeTypeControllerV1Api.getSupportedMimeTypeById(id)) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(SupportedMimeType.class))
                    .build();
        }
    }

    @Override
    public Response updateSupportedMimeTypeById(String id, SupportedMimeTypeCreateUpdate supportedMimeTypeCreateUpdate) {
        try (Response response = supportedMimeTypeControllerV1Api.updateSupportedMimeTypeById(id,
                mapper.map(supportedMimeTypeCreateUpdate))) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(SupportedMimeType.class))
                    .build();
        }
    }
}
