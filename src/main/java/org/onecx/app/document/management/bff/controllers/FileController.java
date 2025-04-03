package org.onecx.app.document.management.bff.controllers;

import java.io.File;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.org.tkit.onecx.document_management.client.api.FileControllerV1Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class FileController implements FileControllerV1Api {
    @Inject
    @RestClient
    FileControllerV1Api fileControllerV1Api;

    @Override
    public Response createBucket(String name) {
        try {
            return fileControllerV1Api.createBucket(name);
        } catch (Exception e) {
            log.error("Error occurred while creating bucket", e);
            throw new RuntimeException("Error occurred while creating bucket", e);
        }
    }

    @Override
    public Response deleteFile(String bucket, String path) {
        try {
            return fileControllerV1Api.deleteFile(bucket, path);
        } catch (Exception e) {
            log.error("Error occurred while deleting file", e);
            throw new RuntimeException("Error occurred while deleting file", e);
        }
    }

    @Override
    public File downloadFile(String bucket, String path) {
        try {
            return fileControllerV1Api.downloadFile(bucket, path);
        } catch (Exception e) {
            log.error("Error occurred while downloading file", e);
            throw new RuntimeException("Error occurred while downloading file", e);
        }
    }

    @Override
    public Response uploadFile(UploadFileMultipartForm multipartForm, String bucket, String path) {
        try {
            return fileControllerV1Api.uploadFile(multipartForm, bucket, path);
        } catch (Exception e) {
            log.error("Error occurred while uploading file", e);
            throw new RuntimeException("Error occurred while uploading file", e);
        }
    }
}
