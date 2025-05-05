package com.federico.negocio.app.auth_service.exceptions;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;


@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        return ErrorResponseFactory.createErrorResponse(UNAUTHORIZED, ex.getMessage() != null ? ex.getMessage() : ConstantsExceptions.AUTHENTICATION_EXCEPTION);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        return ErrorResponseFactory.createErrorResponse(UNAUTHORIZED, ex.getMessage() != null ? ex.getMessage() : ConstantsExceptions.AUTHENTICATION_EXCEPTION);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        return ErrorResponseFactory.createErrorResponse(FORBIDDEN, ex.getMessage() != null ? ex.getMessage() : ConstantsExceptions.AUTHENTICATION_EXCEPTION);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ErrorResponseFactory.createErrorResponse(CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ErrorResponseFactory.createValidationErrorResponse(BAD_REQUEST, errors);
    }
}