
package org.onecx.app.document.management.bff.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.document_management.rs.internal.model.*;

@Mapper
public interface DocumentMapper {
    gen.org.tkit.onecx.document_management.client.model.DocumentCreateUpdate map(DocumentCreateUpdate documentCreateUpdate);

    List<gen.org.tkit.onecx.document_management.client.model.DocumentCreateUpdate> map(
            List<DocumentCreateUpdate> documentCreateUpdate);

    @Mapping(target = "_file", ignore = true)
    gen.org.tkit.onecx.document_management.client.model.AttachmentCreateUpdate map(
            AttachmentCreateUpdate attachmentCreateUpdate);

    gen.org.tkit.onecx.document_management.client.model.LifeCycleState map(LifeCycleState lifeCycleState);

    List<gen.org.tkit.onecx.document_management.client.model.LifeCycleState> mapLifeCycle(List<LifeCycleState> lifeCycleState);

    gen.org.tkit.onecx.document_management.client.model.DocumentSpecificationCreateUpdate map(
            DocumentSpecificationCreateUpdate documentSpecificationCreateUpdate);

    gen.org.tkit.onecx.document_management.client.model.DocumentTypeCreateUpdate map(
            DocumentTypeCreateUpdate documentTypeCreateUpdate);

    gen.org.tkit.onecx.document_management.client.model.SupportedMimeTypeCreateUpdate map(
            SupportedMimeTypeCreateUpdate supportedMimeTypeCreateUpdate);

}
