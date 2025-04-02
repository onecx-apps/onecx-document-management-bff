
package org.onecx.app.document.management.bff.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.mappers.DocumentMapper;

import gen.org.tkit.onecx.document_management.client.api.DocumentSpecificationControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.*;
import gen.org.tkit.onecx.document_management.rs.internal.model.DocumentSpecificationCreateUpdateDTODTO;
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

        DocumentSpecificationCreateUpdateDTODTO documentSpecificationCreateUpdateDTODTO = new DocumentSpecificationCreateUpdateDTODTO();
        documentSpecificationCreateUpdateDTODTO.setName(documentSpecificationCreateUpdateDTO.getName());
        documentSpecificationCreateUpdateDTODTO
                .setSpecificationVersion((documentSpecificationCreateUpdateDTO.getSpecificationVersion()));
        DocumentSpecificationCreateUpdateDTO mappedDto = mapper.map(documentSpecificationCreateUpdateDTODTO);
        log.info("test-svc------------------------------testing" + mappedDto);
        return documentSpecificationControllerV1Api.createDocumentSpecification(mappedDto);
    }

    @Override
    public Response deleteDocumentSpecificationById(String id) {
        return documentSpecificationControllerV1Api.deleteDocumentSpecificationById(id);
    }

    @Override
    public List<DocumentSpecificationDTO> getAllDocumentSpecifications() {
        return documentSpecificationControllerV1Api.getAllDocumentSpecifications();
    }

    @Override
    public DocumentSpecificationDTO getDocumentSpecificationById(String id) {
        return documentSpecificationControllerV1Api.getDocumentSpecificationById(id);
    }

    @Override
    public DocumentSpecificationDTO updateDocumentSpecificationById(String id,
            DocumentSpecificationCreateUpdateDTO documentSpecificationCreateUpdateDTO) {
        return documentSpecificationControllerV1Api.updateDocumentSpecificationById(id, documentSpecificationCreateUpdateDTO);
    }

}
