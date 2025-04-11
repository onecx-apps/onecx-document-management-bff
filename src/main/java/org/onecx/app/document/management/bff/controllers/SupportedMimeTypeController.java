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
import gen.org.tkit.onecx.document_management.rs.internal.model.SupportedMimeTypeCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.rs.internal.model.SupportedMimeTypeDTO;
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
    public Response createSupportedMimeType(SupportedMimeTypeCreateUpdateDTO supportedMimeTypeCreateUpdateDTO) {
        try (Response response = supportedMimeTypeControllerV1Api
                .createSupportedMimeType(mapper.map(supportedMimeTypeCreateUpdateDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity((SupportedMimeTypeDTO.class))))
                    .build();
        }
    }

    @Override
    public Response deleteSupportedMimeTypeId(String id) {
        try (Response response = supportedMimeTypeControllerV1Api.deleteSupportedMimeTypeId(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAllSupportedMimeTypes() {
        try (Response response = supportedMimeTypeControllerV1Api.getAllSupportedMimeTypes()) {
            return Response.status(response.getStatus())
                    .entity(mapper.mapMimeTypeList(response.readEntity(new GenericType<List<SupportedMimeTypeDTO>>() {
                    })))
                    .build();
        }
    }

    @Override
    public Response getSupportedMimeTypeById(String id) {
        try (Response response = supportedMimeTypeControllerV1Api.getSupportedMimeTypeById(id)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(SupportedMimeTypeDTO.class)))
                    .build();
        }
    }

    @Override
    public Response updateSupportedMimeTypeById(String id, SupportedMimeTypeCreateUpdateDTO supportedMimeTypeCreateUpdate) {
        try (Response response = supportedMimeTypeControllerV1Api.updateSupportedMimeTypeById(id,
                mapper.map(supportedMimeTypeCreateUpdate))) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(SupportedMimeTypeDTO.class)))
                    .build();
        }
    }
}
