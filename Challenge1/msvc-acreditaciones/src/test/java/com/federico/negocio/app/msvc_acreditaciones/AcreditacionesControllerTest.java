package com.federico.negocio.app.msvc_acreditaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.federico.negocio.app.msvc_acreditaciones.controller.AcreditacionesController;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionRequest;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionResponse;
import com.federico.negocio.app.msvc_acreditaciones.services.AcreditacionService;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class AcreditacionesControllerTest {

    @Mock
    private AcreditacionService service;

    @InjectMocks
    private AcreditacionesController controller;

    @Test
    @DisplayName("guardarAcreditacion debería retornar el response correcto con estado 201")
    void testGuardarAcreditacion() {
        
        AcreditacionRequest request = new AcreditacionRequest(1, 1000.0);
        AcreditacionResponse expectedResponse = new AcreditacionResponse("1", 1, "CABA", 1000.0, LocalDate.now());

        when(service.guardarAcreditacion(any())).thenReturn(expectedResponse);

        AcreditacionResponse response = controller.guardarAcreditacion(request);
        
        assertEquals("1", response.id());
        assertEquals(1, response.identificadorPuntoVenta());
        assertEquals("CABA", response.nombrePuntoventa());
        assertEquals(1000.0, response.importe());
        assertEquals(expectedResponse.fechaPedido(), response.fechaPedido());
    }

    @Test
    @DisplayName("getAcreditacionById debería retornar el response correcto con estado 200")
    void testGetAcreditacionById() {
        // Arrange
        String id = "1";
        AcreditacionResponse expectedResponse = new AcreditacionResponse(id, 1, "CABA", 1000.0, LocalDate.now());

        when(service.getAcreditacionById(id)).thenReturn(expectedResponse);

        // Act
        AcreditacionResponse response = controller.getAcreditacionById(id);

        // Assert
        assertEquals(id, response.id());
        assertEquals("CABA", response.nombrePuntoventa());
    }

    @Test
    @DisplayName("getAcreditacionById debería devolver 404 cuando no se encuentra la acreditación")
    void testGetAcreditacionByIdNotFound() {
        String id = "1";

        when(service.getAcreditacionById(id)).thenThrow( NotFoundException.build("Acreditacion no encontrada"));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            controller.getAcreditacionById(id);
        });

        assertEquals("Acreditacion no encontrada", thrown.getMessage());
    }
}

    // @DisplayName("Debería devolver 400 cuando importe es nulo o negativo")
    // @Test
    // void deberiaRetornar400SiImporteEsInvalido() {
    // }

    // @DisplayName("Debería devolver 400 con múltiples errores de validación cuando ambos campos son inválidos")
    // @Test
    // void deberiaRetornar400ConMultiplesErroresDeValidacion() {
    // }

    // @DisplayName("Debería devolver 404 cuando el servicio de punto de venta no encuentra el ID")
    // @Test
    // void deberiaRetornar404SiPuntoVentaNoExiste() {
    // }

    // @DisplayName("Debería devolver 500 cuando ocurre un error al comunicarse con el servicio de punto de venta")
    // @Test
    // void deberiaRetornar500PorFalloEnServicioExterno() {
    // }
    
    // @DisplayName("Debería devolver 500 cuando ocurre un error inesperado en el servicio")
    // @Test
    // void deberiaRetornar500PorErrorInterno() {
    // }

    // @DisplayName("Debería devolver 200 y los datos de la acreditación cuando el ID existe")
    // @Test
    // void deberiaDevolver200SiAcreditacionExiste() {
    // }

    // @DisplayName("Debería devolver 404 cuando no se encuentra una acreditación con el ID dado")
    // @Test
    // void deberiaDevolver404SiAcreditacionNoExiste() {
    // }

    // @DisplayName("Debería devolver 500 cuando ocurre un error inesperado al buscar la acreditación por ID")
    // @Test
    // void deberiaDevolver500SiFallaBusquedaDeAcreditacion() {
    // }