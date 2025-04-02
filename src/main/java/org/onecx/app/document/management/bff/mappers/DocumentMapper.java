package org.onecx.app.document.management.bff.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.document_management.client.model.*;
import gen.org.tkit.onecx.document_management.rs.internal.model.*;

@Mapper

public interface DocumentMapper {
    DocumentCreateUpdateDTO map(DocumentCreateUpdateDTODTO documentCreateUpdateDTODTO);

    List<DocumentCreateUpdateDTO> map(List<DocumentCreateUpdateDTODTO> documentCreateUpdateDTODTO);

    @Mapping(target = "_file", ignore = true)
    AttachmentCreateUpdateDTO map(AttachmentCreateUpdateDTODTO attachmentCreateUpdateDTODTO);

    LifeCycleState map(LifeCycleStateDTO lifeCycleStateDTO);

    List<LifeCycleState> mapLifeCycle(List<LifeCycleStateDTO> lifeCycleStateDTO);

    DocumentSpecificationCreateUpdateDTO map(DocumentSpecificationCreateUpdateDTODTO documentSpecificationCreateUpdateDTODTO);

    DocumentTypeCreateUpdateDTO map(DocumentTypeCreateUpdateDTODTO documentTypeCreateUpdateDTODTO);

    SupportedMimeTypeCreateUpdateDTO map(SupportedMimeTypeCreateUpdateDTODTO supportedMimeTypeCreateUpdateDTODTO);

}
