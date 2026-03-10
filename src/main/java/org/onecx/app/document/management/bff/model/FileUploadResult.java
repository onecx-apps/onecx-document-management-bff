package org.onecx.app.document.management.bff.model;

import java.math.BigDecimal;

import gen.org.tkit.onecx.document_management.client.model.AttachmentUnit;
import lombok.Builder;

@Builder
public record FileUploadResult(
        String documentId,
        String attachmentId,
        Integer operationStatusCode,
        BigDecimal attachmentSize,
        AttachmentUnit sizeUnit,
        String storage,
        String type,
        boolean isSuccess) {
}
