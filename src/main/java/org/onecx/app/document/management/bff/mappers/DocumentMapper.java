
package org.onecx.app.document.management.bff.mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.onecx.app.document.management.bff.model.FileUploadResult;

import gen.org.tkit.onecx.document_management.client.model.*;
import gen.org.tkit.onecx.document_management.rs.internal.model.*;
import gen.org.tkit.onecx.filestorage.client.model.PresignedUrlResponse;

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

    DocumentSearchCriteria mapToInternalCriteria(DocumentSearchCriteriaDTO searchCriteriaDTO);

    @Mapping(target = "createdBy", source = "document.creationUser")
    @Mapping(target = "createdDate", source = "document.creationDate")
    @Mapping(target = "lastModifiedBy", source = "document.modificationUser")
    @Mapping(target = "lastModifiedDate", source = "document.modificationDate")
    @Mapping(target = "attachmentResponse", source = "uploadResults", qualifiedByName = "mapAttachmentResponse")
    DocumentResponse map(List<FileUploadResult> uploadResults, DocumentDetail document);

    AttachmentPresignedUrlResponseDTO mapPresignedUrl(PresignedUrlResponse response);

    @Named("mapAttachmentResponse")
    default Map<String, Integer> mapAttachmentResponse(List<FileUploadResult> uploadResults) {
        return uploadResults.stream().collect(Collectors.toMap(
                FileUploadResult::attachmentId,
                FileUploadResult::operationStatusCode));
    }

}
