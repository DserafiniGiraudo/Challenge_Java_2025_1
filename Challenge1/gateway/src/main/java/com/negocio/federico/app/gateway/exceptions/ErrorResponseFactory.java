package com.negocio.federico.app.gateway.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.Collections;

public final class ErrorResponseFactory {

    private ErrorResponseFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.ok(Collections.singletonMap("message", message));
    }

    public static ResponseEntity<Map<String, String>> createValidationErrorResponse(HttpStatus status, Map<String, String> errors) {
        return ResponseEntity.ok(Map.copyOf(errors));
    }
}