package com.federico.negocio.app.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

//Factory Pattern
public final class ErrorResponseFactory {

    private ErrorResponseFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }

    public static ResponseEntity<Map<String, String>> createValidationErrorResponse(HttpStatus status, Map<String, String> errors) {
        return new ResponseEntity<>(errors, status);
    }
}