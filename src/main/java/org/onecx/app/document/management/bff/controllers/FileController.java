package org.onecx.app.document.management.bff.controllers;

import java.io.File;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.org.tkit.onecx.document_management.client.api.FileControllerV1Api;
import gen.org.tkit.onecx.document_management.rs.internal.FileControllerV1ApiService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class FileController implements FileControllerV1ApiService {
    @Inject
    @RestClient
    FileControllerV1Api fileControllerV1Api;

    @Override
    public Response createBucket(String name) {
        try (Response response = fileControllerV1Api.createBucket(name)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response deleteFile(String bucket, String path) {
        try (Response response = fileControllerV1Api.deleteFile(bucket, path)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response downloadFile(String bucket, String path) {
        try (Response response = fileControllerV1Api.downloadFile(bucket, path)) {
            return Response.status(response.getStatus())
                    .entity(response.readEntity(File.class))
                    .build();
        }
    }

    @Override
    public Response uploadFile(String bucket, String path, File _file) {
        FileControllerV1Api.UploadFileMultipartForm multi = new FileControllerV1Api.UploadFileMultipartForm();
        multi._file = _file;
        try (Response response = fileControllerV1Api.uploadFile(multi, bucket, path)) {
            return Response.status(response.getStatus()).build();
        }
    }
}
