package com.federico.negocio.app.msvc_acreditaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.federico.negocio.app.msvc_acreditaciones.controller.GlobalExceptionHandler;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.ConflictException;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private WebClientResponseException webClientResponseException;

    @Mock
    private WebClientException webClientException;

    @Mock
    private DataAccessException dataAccessException;

    @Mock
    private ConflictException conflictException;

    @Mock
    private NotFoundException notFoundException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleValidationExceptions() {
        // Arrange
        FieldError fieldError = new FieldError("objectName", "fieldName", "Field is required");

        // Configurar el mock para que devuelva el BindingResult y luego los FieldErrors
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(((Map<String, String>) response.getBody()).containsKey("fieldName"));
        assertEquals("Field is required", ((Map<String, String>) response.getBody()).get("fieldName"));
    }

    @Test
    void testHandleConflictException() {
        // Arrange
        String errorMessage = "Conflict occurred";
        when(conflictException.getMessage()).thenReturn(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleConflictException(conflictException);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(((Map<String, String>) response.getBody()).containsKey("error"));
        assertEquals(errorMessage, ((Map<String, String>) response.getBody()).get("error"));
    }

    @Test
    void testHandleWebClientResponseException() {
        // Arrange
        String responseBody = "Service unavailable";
        when(webClientResponseException.getResponseBodyAsString()).thenReturn(responseBody);
        when(webClientResponseException.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleWebClientResponseException(webClientResponseException);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(responseBody, response.getBody());
    }

    @Test
    void testHandleWebClientException() {
        // Arrange
        String errorMessage = "Service communication error";
        when(webClientException.getMessage()).thenReturn(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleWebClientException(webClientException);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(((Map<String, String>) response.getBody()).containsKey("error"));
        assertEquals("Error general al comunicarse con otro servicio: " + errorMessage, ((Map<String, String>) response.getBody()).get("error"));
    }

    @Test
    void testHandleGeneralExceptions() {
        // Arrange
        String errorMessage = "An unexpected error occurred";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleGeneralExceptions(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(((Map<String, String>) response.getBody()).containsKey("error"));
        assertEquals(errorMessage, ((Map<String, String>) response.getBody()).get("error"));
    }

    @Test
    @DisplayName("handleNotFoundException - Debe retornar un error 404 cuando no se encuentra el recurso")
    void testHandleNotFoundException() {
        // Arrange
        String errorMessage = "Resource not found";
        
        // Configuramos el mock para que devuelva el mensaje esperado
        when(notFoundException.getMessage()).thenReturn(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleConflictException(notFoundException);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());  // Verificamos que el código de estado sea 404
        assertTrue(((Map<String, String>) response.getBody()).containsKey("error"));  // Verificamos que el error esté presente en el cuerpo
        assertEquals(errorMessage, ((Map<String, String>) response.getBody()).get("error"));  // Verificamos que el mensaje de error sea el correcto
    }
}
