package com.federico.negocio.app.msvc_acreditaciones.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.federico.negocio.libs.commons.libs_msvc_commons.exception.ConflictException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.CONFLICT;
        return new ResponseEntity<>(errors, status);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex) {
        String responseBody = ex.getResponseBodyAsString();
        HttpStatus statusCode = (HttpStatus) ex.getStatusCode();

        return new ResponseEntity<>(responseBody, statusCode);
    }

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<Object> handleWebClientException(WebClientException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Error general al comunicarse con otro servicio: " + (ex.getMessage() != null ? ex.getMessage() : "Error inesperado."));
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage() == null ? "Ocurrió un error inesperado." : ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDataAccessExceptions(DataAccessException ex) {
        Map<String, String> errors = new HashMap<>();
        String errorMessage = "Ocurrió un error al acceder a los datos. Por favor, contacte al administrador.";
        errors.put("error", errorMessage);
        log.error("Error de acceso a datos", ex);
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
