package org.onecx.app.document.management.bff.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Response;

import lombok.Getter;

@Getter
public class RestException extends RuntimeException {

    private Response.Status status;
    private ErrorCode errorCode;
    private final List<Object> parameters = new ArrayList<>();
    private final Map<String, Object> namedParameters = new HashMap<>();

    public RestException(final Response.Status status, final String message, final ErrorCode errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
