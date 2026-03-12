package org.onecx.app.document.management.bff.model;

import gen.org.tkit.onecx.filestorage.client.model.FileMetadataResponse;

public record MetadataResult(String attachmentId, FileMetadataResponse response) {
}
