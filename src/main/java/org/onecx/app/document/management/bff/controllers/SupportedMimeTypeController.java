package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;

import gen.org.tkit.onecx.document_management.client.api.SupportedMimeTypeControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.SupportedMimeTypeCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.client.model.SupportedMimeTypeDTO;

public class SupportedMimeTypeController implements SupportedMimeTypeControllerV1Api {

    @Inject
    @RestClient
    SupportedMimeTypeControllerV1Api supportedMimeTypeControllerV1Api;
    @Inject
    DocumentMapper mapper;

    @Override
    public SupportedMimeTypeDTO createSupportedMimeType(SupportedMimeTypeCreateUpdateDTO supportedMimeTypeCreateUpdateDTO) {
        return supportedMimeTypeControllerV1Api.createSupportedMimeType(supportedMimeTypeCreateUpdateDTO);
    }

    @Override
    public Response deleteSupportedMimeTypeId(String id) {
        return supportedMimeTypeControllerV1Api.deleteSupportedMimeTypeId(id);
    }

    @Override
    public List<SupportedMimeTypeDTO> getAllSupportedMimeTypes() {
        return supportedMimeTypeControllerV1Api.getAllSupportedMimeTypes();
    }

    @Override
    public SupportedMimeTypeDTO getSupportedMimeTypeById(String id) {
        return supportedMimeTypeControllerV1Api.getSupportedMimeTypeById(id);
    }

    @Override
    public SupportedMimeTypeDTO updateSupportedMimeTypeById(String id,
            SupportedMimeTypeCreateUpdateDTO supportedMimeTypeCreateUpdateDTO) {
        return supportedMimeTypeControllerV1Api.updateSupportedMimeTypeById(id, supportedMimeTypeCreateUpdateDTO);
    }
}
