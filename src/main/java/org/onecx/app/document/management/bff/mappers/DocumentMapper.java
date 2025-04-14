
package org.onecx.app.document.management.bff.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.document_management.client.model.*;
import gen.org.tkit.onecx.document_management.rs.internal.model.*;

@Mapper
public interface DocumentMapper {
    DocumentCreateUpdate map(DocumentCreateUpdateDTO documentCreateUpdateDTO);

    List<DocumentCreateUpdate> map(List<DocumentCreateUpdateDTO> documentCreateUpdateDTO);

    @Mapping(target = "_file", ignore = true)
    AttachmentCreateUpdate map(AttachmentCreateUpdateDTO attachmentCreateUpdateDTO);

    LifeCycleState map(LifeCycleStateDTO lifeCycleStateDTO);

    List<LifeCycleState> mapLifeCycle(List<LifeCycleStateDTO> lifeCycleStateDTO);

    DocumentSpecificationCreateUpdate map(DocumentSpecificationCreateUpdateDTO documentSpecificationCreateUpdateDTO);

    DocumentTypeCreateUpdate map(DocumentTypeCreateUpdateDTO documentTypeCreateUpdateDTO);

    SupportedMimeType map(SupportedMimeTypeDTO supportedMimeTypeDTO);

    List<SupportedMimeType> mapMimeTypeList(List<SupportedMimeTypeDTO> supportedMimeTypeDTOS);

    SupportedMimeTypeCreateUpdate map(SupportedMimeTypeCreateUpdateDTO supportedMimeTypeCreateUpdateDTO);

    DocumentDetail map(DocumentDetailDTO documentDetailDTO);

    List<DocumentDetail> mapDetailList(List<DocumentDetailDTO> documentDetailDTOList);

    PageResult map(PageResultDTO pageResultDTO);

    Channel map(ChannelDTO channelDTO);

    List<Channel> mapChannel(List<ChannelDTO> channelDTOS);

    List<StorageUploadAudit> mapAuditList(List<StorageUploadAuditDTO> storageUploadAuditDTOS);

    DocumentResponse map(DocumentResponseDTO documentResponseDTO);

    DocumentSpecification map(DocumentSpecificationDTO documentSpecificationDTO);

    List<DocumentSpecification> mapSpecification(List<DocumentSpecificationDTO> documentSpecificationDTOS);

    List<DocumentType> mapType(List<DocumentTypeDTO> documentTypeDTOS);

    DocumentType mapDocumentType(DocumentTypeDTO documentTypeDTO);

}
