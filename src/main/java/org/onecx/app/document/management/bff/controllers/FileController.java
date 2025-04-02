package org.onecx.app.document.management.bff.controllers;

import java.io.File;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.org.tkit.onecx.document_management.client.api.FileControllerV1Api;

public class FileController implements FileControllerV1Api {
    @Inject
    @RestClient
    FileControllerV1Api fileControllerV1Api;

    @Override
    public Response createBucket(String name) {
        return fileControllerV1Api.createBucket(name);
    }

    @Override
    public Response deleteFile(String bucket, String path) {
        return fileControllerV1Api.deleteFile(bucket, path);
    }

    @Override
    public File downloadFile(String bucket, String path) {
        return fileControllerV1Api.downloadFile(bucket, path);
    }

    @Override
    public Response uploadFile(UploadFileMultipartForm multipartForm, String bucket, String path) {
        return fileControllerV1Api.uploadFile(multipartForm, bucket, path);
    }
}
