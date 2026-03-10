package org.onecx.app.document.management.bff.model;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder
public record UploadUrlResult(
        String documentId,
        String attachmentId,
        Integer operationStatusCode,
        String url,
        OffsetDateTime expiration) {
}
