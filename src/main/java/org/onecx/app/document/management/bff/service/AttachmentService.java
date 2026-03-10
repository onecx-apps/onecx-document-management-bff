package org.onecx.app.document.management.bff.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.server.multipart.FormValue;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataInput;
import org.onecx.app.document.management.bff.model.FileUploadResult;

import gen.org.tkit.onecx.document_management.client.api.AttachmentControllerV1Api;
import gen.org.tkit.onecx.document_management.client.model.Attachment;
import gen.org.tkit.onecx.document_management.client.model.AttachmentUnit;
import gen.org.tkit.onecx.document_management.client.model.DocumentDetail;
import gen.org.tkit.onecx.filestorage.client.api.FileStorageApi;
import gen.org.tkit.onecx.filestorage.client.model.PresignedUrlRequest;
import gen.org.tkit.onecx.filestorage.client.model.PresignedUrlResponse;

@ApplicationScoped
public class AttachmentService {

    @Inject
    @RestClient
    FileStorageApi fileStorageApi;

    @Inject
    @RestClient
    AttachmentControllerV1Api attachmentClient;

    private static final String NAME_DIVIDER = "_";
    private static final String STRING_TOKEN_DELIMITER = ",";
    private static final String FORM_DATA_MAP_KEY = "file";
    private static final String PRODUCT_NAME = "onecx-document-management";
    private static final String APP_NAME = "onecx-document-management-bff";
    private static final String STORAGE_TYPE = "rustfs";

    public List<FileUploadResult> uploadAllFiles(final DocumentDetail documentDetail, MultipartFormDataInput dataInput) {
        final var mediaType = resolveMediaType(dataInput);
        final var inputParts = dataInput.getValues().get(FORM_DATA_MAP_KEY);
        final var attachmentsToProcess = resolveAttachmentsToProcess(documentDetail, inputParts, mediaType);
        final List<FileUploadResult> results = new ArrayList<>();
        attachmentsToProcess.forEach(att -> results.add(processAttachment(documentDetail, att, inputParts)));
        return results;
    }

    public PresignedUrlResponse getFilePresignedUrl(final String attachmentId) {
        final var attachment = getAttachment(attachmentId);
        final var downloadRequest = getDownloadURLRequest(attachment);
        return getPresignedUrl(downloadRequest);
    }

    private FileUploadResult processAttachment(final DocumentDetail documentDetail, final Attachment attachment,
            final Collection<FormValue> inputParts) {
        final var matchedInputPartOptional = inputParts.stream()
                .filter(inputPart -> attachment.getFileName().equals(inputPart.getFileName()))
                .findFirst();
        if (matchedInputPartOptional.isEmpty()) {
            return getResult(documentDetail.getId(), attachment.getId());
        }
        final var inputPart = matchedInputPartOptional.get();
        try {
            InputStream inputPartBody = inputPart.getFileItem().getInputStream();
            byte[] fileBytes = IOUtils.toByteArray(inputPartBody);
            String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(fileBytes));
            uploadAttachment(fileBytes, attachment.getId(), attachment.getFileName());
            return getResult(documentDetail.getId(), attachment.getId(), Response.Status.CREATED.getStatusCode(),
                    BigDecimal.valueOf(fileBytes.length), AttachmentUnit.BYTES, STORAGE_TYPE, contentType, true);
        } catch (Exception e) {
            return getResult(documentDetail.getId(), attachment.getId());
        }

    }

    private void uploadAttachment(final byte[] fileBytes, final String attachmentId, final String fileName)
            throws IOException {
        var tempFile = File.createTempFile(attachmentId, fileName);
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), fileBytes);
        var finalFileName = getUploadFileName(attachmentId, fileName);
        var request = getUploadRequest(tempFile, finalFileName);
        try (var ignored = fileStorageApi.uploadFile(request)) {
        }
    }

    private String getUploadFileName(final String attachmentId, final String fileName) {
        return String.format("%s%s%s", attachmentId, NAME_DIVIDER, fileName);
    }

    private String resolveMediaType(MultipartFormDataInput input) {
        for (Map.Entry<String, Collection<FormValue>> attribute : input.getValues().entrySet()) {
            for (FormValue fv : attribute.getValue()) {
                if (fv.isFileItem()) {
                    return fv.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
                }
            }
        }
        return "";
    }

    private Set<Attachment> resolveAttachmentsToProcess(DocumentDetail document, Collection<FormValue> inputParts,
            String mediaType) {
        Set<Attachment> attachmentSet = new HashSet<>();
        if (String.valueOf(MediaType.valueOf(mediaType)).equals("text/plain")) {
            List<String> attachmentIdList = getAttachmentIdList(inputParts.stream().toList());
            inputParts.remove(0);
            attachmentIdList.forEach(attachmentId -> document.getAttachments().stream()
                    .filter(attachment -> attachmentId.equals(attachment.getId()))
                    .findFirst()
                    .ifPresent(attachmentSet::add));
        } else {
            attachmentSet.addAll(document.getAttachments());
        }
        return attachmentSet;
    }

    private List<String> getAttachmentIdList(List<FormValue> inputPartList) {
        List<String> attachmentIdList = new ArrayList<>();
        var stringTokenizer = new StringTokenizer(String.valueOf(inputPartList.get(0).getFileItem()),
                STRING_TOKEN_DELIMITER);
        while (stringTokenizer.hasMoreTokens()) {
            attachmentIdList.add(stringTokenizer.nextToken());
        }
        return attachmentIdList;
    }

    private FileUploadResult getResult(final String documentId, final String attachmentId) {
        return getResult(documentId, attachmentId, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                null, null, null, null, false);
    }

    private FileUploadResult getResult(final String documentId, final String attachmentId, final Integer code,
            final BigDecimal attachmentSize, final AttachmentUnit sizeUnit, final String storage,
            final String type, final boolean isSuccess) {
        return new FileUploadResult(documentId, attachmentId, code, attachmentSize, sizeUnit, storage, type, isSuccess);
    }

    private FileStorageApi.UploadFileMultipartForm getUploadRequest(final File file, final String fileName) {
        final var request = new FileStorageApi.UploadFileMultipartForm();
        request.applicationId = APP_NAME;
        request.productName = PRODUCT_NAME;
        request._file = file;
        request.fileName = fileName;
        return request;
    }

    private PresignedUrlRequest getDownloadURLRequest(final Attachment attachment) {
        final var fileName = getUploadFileName(attachment.getId(), attachment.getFileName());
        final var request = new PresignedUrlRequest();
        request.setApplicationId(APP_NAME);
        request.setProductName(PRODUCT_NAME);
        request.setFileName(fileName);
        return request;
    }

    private Attachment getAttachment(final String attachmentId) {
        final var attResponse = attachmentClient.getAttachmentDetails(attachmentId);
        if (attResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            throw new NotFoundException("Attachment not found");
        }
        if (attResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new InternalServerErrorException();
        }
        return attResponse.readEntity(Attachment.class);
    }

    private PresignedUrlResponse getPresignedUrl(final PresignedUrlRequest downloadRequest) {
        var response = fileStorageApi.getPresignedDownloadUrl(downloadRequest);
        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            throw new BadRequestException("File could not be downloaded");
        }
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new InternalServerErrorException();
        }
        return response.readEntity(PresignedUrlResponse.class);
    }
}
