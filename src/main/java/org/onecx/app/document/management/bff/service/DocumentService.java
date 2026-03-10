package org.onecx.app.document.management.bff.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.onecx.app.document.management.bff.model.FileUploadResult;

import gen.org.tkit.onecx.document_management.client.api.DocumentControllerV1Api;

@ApplicationScoped
public class DocumentService {
    @Inject
    @RestClient
    DocumentControllerV1Api documentControllerV1Api;

    public void persistFileUploadResults(final List<FileUploadResult> results) {
        final var successfulResults = results.stream()
                .filter(FileUploadResult::isSuccess).toList();
        final var failures = results.stream()
                .filter(result -> !result.isSuccess()).toList();
        if (!successfulResults.isEmpty()) {
            persistSuccesses(results);
        }
        if (!failures.isEmpty()) {
            persistFailures(failures);
        }
    }

    private void persistSuccesses(final List<FileUploadResult> results) {
        // TODO implement
    }

    private void persistFailures(final List<FileUploadResult> results) {
        // TODO implement
    }
}
