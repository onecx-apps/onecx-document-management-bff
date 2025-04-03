package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;

import gen.org.tkit.onecx.document_management.client.api.SupportedMimeTypeControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.SupportedMimeTypeCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.client.model.SupportedMimeTypeDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class SupportedMimeTypeController implements SupportedMimeTypeControllerV1Api {
    @Inject
    @RestClient
    SupportedMimeTypeControllerV1Api supportedMimeTypeControllerV1Api;

    @Inject
    DocumentMapper mapper;

    @Override
    public SupportedMimeTypeDTO createSupportedMimeType(SupportedMimeTypeCreateUpdateDTO supportedMimeTypeCreateUpdateDTO) {
        try {
            return supportedMimeTypeControllerV1Api.createSupportedMimeType(supportedMimeTypeCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while creating supported mime type", e);
            throw new RuntimeException("Error occurred while creating supported mime type", e);
        }
    }

    @Override
    public Response deleteSupportedMimeTypeId(String id) {
        try {
            return supportedMimeTypeControllerV1Api.deleteSupportedMimeTypeId(id);
        } catch (Exception e) {
            log.error("Error occurred while deleting supported mime type", e);
            throw new RuntimeException("Error occurred while deleting supported mime type", e);
        }
    }

    @Override
    public List<SupportedMimeTypeDTO> getAllSupportedMimeTypes() {
        try {
            return supportedMimeTypeControllerV1Api.getAllSupportedMimeTypes();
        } catch (Exception e) {
            log.error("Error occurred while fetching all supported mime types", e);
            throw new RuntimeException("Error occurred while fetching all supported mime types", e);
        }
    }

    @Override
    public SupportedMimeTypeDTO getSupportedMimeTypeById(String id) {
        try {
            return supportedMimeTypeControllerV1Api.getSupportedMimeTypeById(id);
        } catch (Exception e) {
            log.error("Error occurred while fetching supported mime type", e);
            throw new RuntimeException("Error occurred while fetching supported mime type", e);
        }
    }

    @Override
    public SupportedMimeTypeDTO updateSupportedMimeTypeById(String id,
            SupportedMimeTypeCreateUpdateDTO supportedMimeTypeCreateUpdateDTO) {
        try {
            return supportedMimeTypeControllerV1Api.updateSupportedMimeTypeById(id, supportedMimeTypeCreateUpdateDTO);
        } catch (Exception e) {
            log.error("Error occurred while updating supported mime type", e);
            throw new RuntimeException("Error occurred while updating supported mime type", e);
        }
    }
}
