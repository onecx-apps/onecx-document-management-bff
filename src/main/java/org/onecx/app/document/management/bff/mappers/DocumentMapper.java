package org.onecx.app.document.management.bff.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.document_management.client.model.*;
import gen.org.tkit.onecx.document_management.client.model.AttachmentCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.client.model.DocumentCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.client.model.DocumentSpecificationCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.client.model.DocumentTypeCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.client.model.SupportedMimeTypeCreateUpdateDTO;
import gen.org.tkit.onecx.document_management.rs.internal.model.*;

@Mapper
public interface DocumentMapper {
    DocumentCreateUpdateDTO map(DocumentCreateUpdateDTO documentCreateUpdateDTODTO);

    List<DocumentCreateUpdateDTO> map(List<DocumentCreateUpdateDTO> documentCreateUpdateDTODTO);

    @Mapping(target = "_file", ignore = true)
    AttachmentCreateUpdateDTO map(AttachmentCreateUpdateDTO attachmentCreateUpdateDTODTO);

    LifeCycleState map(LifeCycleStateDTO lifeCycleStateDTO);

    List<LifeCycleState> mapLifeCycle(List<LifeCycleStateDTO> lifeCycleStateDTO);

    DocumentSpecificationCreateUpdateDTO map(DocumentSpecificationCreateUpdateDTO documentSpecificationCreateUpdateDTODTO);

    DocumentTypeCreateUpdateDTO map(DocumentTypeCreateUpdateDTO documentTypeCreateUpdateDTODTO);

    SupportedMimeTypeCreateUpdateDTO map(SupportedMimeTypeCreateUpdateDTO supportedMimeTypeCreateUpdateDTODTO);

}
