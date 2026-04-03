package org.onecx.app.document.management.bff.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.onecx.app.document.management.bff.AbstractTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gen.org.tkit.onecx.document_management.client.model.Attachment;
import gen.org.tkit.onecx.document_management.client.model.DocumentDetail;
import gen.org.tkit.onecx.filestorage.client.model.FileMetadataResponse;
import gen.org.tkit.onecx.filestorage.client.model.PresignedUrlResponse;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(DocumentController.class)
public class DocumentControllerTest extends AbstractTest {

    private static final String DOCUMENT_ID = "test-document-id";
    private static final String ATTACHMENT_ID = "test-attachment-id";
    private static final String FILE_NAME = "test-file.pdf";
    private static final String PRESIGNED_URL = "https://mock-storage.example.com/download/test-file.pdf";
    private static final OffsetDateTime EXPIRATION = OffsetDateTime.parse("2026-12-31T23:59:59+01:00");
    private static final String USERNAME_TOKEN = "apm-username";
    private static final String SVC_MOCK_ID = "DOC_MGMT_SVC_MOCK";
    private static final String SEC_SVC_MOCK_ID = "SEC_DOC_MGMT_SVC_MOCK";
    private static final String FILE_STORAGE_MOCK_ID = "FILE_STORAGE_SVC_MOCK";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @BeforeEach
    public void resetExpectation() {
        try {
            mockServerClient.clear(SVC_MOCK_ID);
            mockServerClient.clear(FILE_STORAGE_MOCK_ID);
            mockServerClient.clear(SEC_SVC_MOCK_ID);
        } catch (Exception e) {
            // mockid not existing
        }
    }

    @Test
    @DisplayName("GET /file/{attachmentId} - should return presigned download URL when attachment and file storage respond successfully")
    void getFile_shouldReturnPresignedUrl_whenAttachmentAndStorageRespondOk() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName(FILE_NAME);
        attachment.setName("Test Attachment");

        var presignedUrlResponse = new PresignedUrlResponse();
        presignedUrlResponse.setUrl(PRESIGNED_URL);
        presignedUrlResponse.setExpiration(EXPIRATION);

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/attachment/" + ATTACHMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(attachment))));

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/file-storage/presigned/download"))
                .withId(FILE_STORAGE_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(presignedUrlResponse))));

        var responseBody = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .get("/file/{attachmentId}", ATTACHMENT_ID)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .body()
                .jsonPath();

        assertThat(responseBody.getString("url")).isEqualTo(PRESIGNED_URL);
        assertThat(responseBody.getString("expiration")).isNotBlank();
    }

    @Test
    @DisplayName("GET /file/{attachmentId} - should return 404 when attachment does not exist")
    void getFile_shouldReturnNotFound_whenAttachmentNotFound() {
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/attachment/" + ATTACHMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.NOT_FOUND.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .get("/file/{attachmentId}", ATTACHMENT_ID)
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("GET /file/{attachmentId} - should return 400 when attachment service returns a non-404 error")
    void getFile_shouldReturnBadRequest_whenAttachmentReturnsServerError() {
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/attachment/" + ATTACHMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .get("/file/{attachmentId}", ATTACHMENT_ID)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @DisplayName("GET /file/{attachmentId} - should return 400 when file storage fails to provide presigned download URL")
    void getFile_shouldReturnBadRequest_whenPresignedDownloadUrlFails() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName(FILE_NAME);

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/attachment/" + ATTACHMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(attachment))));

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/file-storage/presigned/download"))
                .withId(FILE_STORAGE_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.BAD_REQUEST.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .get("/file/{attachmentId}", ATTACHMENT_ID)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    // ==================== deleteDocumentById ====================

    @Test
    @DisplayName("DELETE /{id} - should return 204 when document with attachments is successfully deleted and files are removed from storage")
    void deleteDocumentById_shouldReturnNoContent_whenDocumentAndFilesDeletedSuccessfully() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName(FILE_NAME);

        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of(attachment));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        mockServerClient
                .when(request()
                        .withMethod("DELETE")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SEC_SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.NO_CONTENT.getStatusCode()));

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/file-storage/file/delete"))
                .withId(FILE_STORAGE_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .delete("/{id}", DOCUMENT_ID)
                .then()
                .statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /{id} - should return 204 when document has no attachments")
    void deleteDocumentById_shouldReturnNoContent_whenDocumentHasNoAttachments() throws Exception {
        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of());

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        mockServerClient
                .when(request()
                        .withMethod("DELETE")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SEC_SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.NO_CONTENT.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .delete("/{id}", DOCUMENT_ID)
                .then()
                .statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /{id} - should return 400 when file storage fails to delete attachment file")
    void deleteDocumentById_shouldReturnBadRequest_whenFileStorageDeleteFails() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName(FILE_NAME);

        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of(attachment));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        mockServerClient
                .when(request()
                        .withMethod("DELETE")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SEC_SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.NO_CONTENT.getStatusCode()));

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/file-storage/file/delete"))
                .withId(FILE_STORAGE_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .delete("/{id}", DOCUMENT_ID)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    // ==================== uploadAllFiles ====================

    @Test
    @DisplayName("POST /files/upload/{documentId} - should return presigned upload URLs for all matched attachments")
    void uploadAllFiles_shouldReturnPresignedUploadUrls_whenAllSucceed() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName(FILE_NAME);

        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of(attachment));

        var presignedUploadResponse = new PresignedUrlResponse();
        presignedUploadResponse.setUrl(PRESIGNED_URL);
        presignedUploadResponse.setExpiration(EXPIRATION);

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/file-storage/presigned/upload"))
                .withId(FILE_STORAGE_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(presignedUploadResponse))));

        var uploadRequest = new gen.org.tkit.onecx.document_management.rs.internal.model.UploadAttachmentPresignedUrlRequestDTO();
        uploadRequest.setAttachmentId(ATTACHMENT_ID);
        uploadRequest.setFileName(FILE_NAME);

        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(List.of(uploadRequest)))
                .post("/files/upload/{documentId}", DOCUMENT_ID)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .body()
                .jsonPath();

        assertThat(response.getList("$")).hasSize(1);
        assertThat(response.getString("[0].url")).isEqualTo(PRESIGNED_URL);
        assertThat(response.getString("[0].attachmentId")).isEqualTo(ATTACHMENT_ID);
    }

    @Test
    @DisplayName("POST /files/upload/{documentId} - should return failed result when attachment file name does not match the request")
    void uploadAllFiles_shouldReturnFailedResult_whenFileNameDoesNotMatch() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName("different-file-name.pdf");

        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of(attachment));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        var uploadRequest = new gen.org.tkit.onecx.document_management.rs.internal.model.UploadAttachmentPresignedUrlRequestDTO();
        uploadRequest.setAttachmentId(ATTACHMENT_ID);
        uploadRequest.setFileName(FILE_NAME);

        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(List.of(uploadRequest)))
                .post("/files/upload/{documentId}", DOCUMENT_ID)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .body()
                .jsonPath();

        assertThat(response.getList("$")).hasSize(1);
        assertThat(response.getString("[0].url")).isNull();
        assertThat(response.getString("[0].attachmentId")).isEqualTo(ATTACHMENT_ID);
    }

    @Test
    @DisplayName("POST /files/upload/{documentId} - should return empty list when no document attachment matches the request")
    void uploadAllFiles_shouldReturnBadRequestStatus_whenAttachmentNotMatchingDocument() throws Exception {
        var attachment = new Attachment();
        attachment.setId("other-attachment-id");
        attachment.setFileName(FILE_NAME);

        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of(attachment));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        var uploadRequest = new gen.org.tkit.onecx.document_management.rs.internal.model.UploadAttachmentPresignedUrlRequestDTO();
        uploadRequest.setAttachmentId(ATTACHMENT_ID);
        uploadRequest.setFileName(FILE_NAME);

        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(List.of(uploadRequest)))
                .post("/files/upload/{documentId}", DOCUMENT_ID)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .body()
                .jsonPath();

        assertThat(response.getList("$")).hasSize(0);
    }

    @Test
    @DisplayName("POST /files/upload/{documentId} - should return failed result when file storage fails to provide presigned upload URL")
    void uploadAllFiles_shouldReturnFailedResult_whenPresignedUploadUrlFails() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName(FILE_NAME);

        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of(attachment));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/file-storage/presigned/upload"))
                .withId(FILE_STORAGE_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()));

        var uploadRequest = new gen.org.tkit.onecx.document_management.rs.internal.model.UploadAttachmentPresignedUrlRequestDTO();
        uploadRequest.setAttachmentId(ATTACHMENT_ID);
        uploadRequest.setFileName(FILE_NAME);

        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(List.of(uploadRequest)))
                .post("/files/upload/{documentId}", DOCUMENT_ID)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .body()
                .jsonPath();

        assertThat(response.getList("$")).hasSize(1);
        assertThat(response.getString("[0].url")).isNull();
    }

    // ==================== updateAttachmentsMetadata ====================

    @Test
    @DisplayName("PATCH /{documentId}/files/metadata - should return 200 when metadata is successfully updated in file storage and attachment service")
    void updateAttachmentsMetadata_shouldReturn200_whenAllSucceed() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName(FILE_NAME);

        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of(attachment));

        var metadataResponse = new FileMetadataResponse();
        metadataResponse.setFileName(ATTACHMENT_ID + "_" + FILE_NAME);
        metadataResponse.setSize(1024L);
        metadataResponse.setType("application/pdf");

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/file-storage/file/metadata"))
                .withId(FILE_STORAGE_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(List.of(metadataResponse)))));

        mockServerClient
                .when(request()
                        .withMethod("PATCH")
                        .withPath("/v1/attachment/metadata"))
                .withId(SEC_SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode()));

        var metadataRequest = new gen.org.tkit.onecx.document_management.rs.internal.model.UpdateFileMetadataRequestDTO();
        metadataRequest.setAttachmentId(ATTACHMENT_ID);

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(List.of(metadataRequest)))
                .patch("/{documentId}/files/metadata", DOCUMENT_ID)
                .then()
                .statusCode(Status.OK.getStatusCode());
    }

    @Test
    @DisplayName("PATCH /{documentId}/files/metadata - should return 400 when file storage fails to return metadata")
    void updateAttachmentsMetadata_shouldReturnBadRequest_whenFileStorageMetadataFails() throws Exception {
        var attachment = new Attachment();
        attachment.setId(ATTACHMENT_ID);
        attachment.setFileName(FILE_NAME);

        var documentDetail = new DocumentDetail();
        documentDetail.setId(DOCUMENT_ID);
        documentDetail.setAttachments(List.of(attachment));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/document/" + DOCUMENT_ID))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(MAPPER.writeValueAsString(documentDetail))));

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/file-storage/file/metadata"))
                .withId(FILE_STORAGE_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()));

        var metadataRequest = new gen.org.tkit.onecx.document_management.rs.internal.model.UpdateFileMetadataRequestDTO();
        metadataRequest.setAttachmentId(ATTACHMENT_ID);

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(List.of(metadataRequest)))
                .patch("/{documentId}/files/metadata", DOCUMENT_ID)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    // ==================== createFailedAttachmentsAuditLogs ====================

    @Test
    @DisplayName("PATCH /{documentId}/files/audit-log - should return 200 when audit logs are successfully created")
    void createFailedAttachmentsAuditLogs_shouldReturn200_whenAuditSucceeds() throws Exception {
        var auditRequest = new gen.org.tkit.onecx.document_management.rs.internal.model.UpdateFileMetadataRequestDTO();
        auditRequest.setAttachmentId(ATTACHMENT_ID);

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/attachment/storage-audit"))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.OK.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(List.of(auditRequest)))
                .patch("/{documentId}/files/audit-log", DOCUMENT_ID)
                .then()
                .statusCode(Status.OK.getStatusCode());
    }

    @Test
    @DisplayName("PATCH /{documentId}/files/audit-log - should return 500 when attachment service fails to create audit logs")
    void createFailedAttachmentsAuditLogs_shouldReturnBadRequest_whenAuditFails() throws Exception {
        var auditRequest = new gen.org.tkit.onecx.document_management.rs.internal.model.UpdateFileMetadataRequestDTO();
        auditRequest.setAttachmentId(ATTACHMENT_ID);

        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/v1/attachment/storage-audit"))
                .withId(SVC_MOCK_ID)
                .respond(response()
                        .withStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(USERNAME_TOKEN, ADMIN)
                .header(APM_HEADER_PARAM, createToken(ADMIN, "org1"))
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(List.of(auditRequest)))
                .patch("/{documentId}/files/audit-log", DOCUMENT_ID)
                .then()
                .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
