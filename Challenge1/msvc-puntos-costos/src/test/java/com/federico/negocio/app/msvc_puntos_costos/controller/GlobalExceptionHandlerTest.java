package com.federico.negocio.app.msvc_puntos_costos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoPKRequest;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoRequest;
import com.federico.negocio.app.msvc_puntos_costos.services.CostosService;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.ConflictException;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

@WebMvcTest(CostosController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CostosService service;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void cuandoSeIntentaCargarCaminoExistente_retornaConflict() throws Exception {
        CaminoRequest request = new CaminoRequest(
                CaminoPK.builder()
                        .puntoA(new PuntoVenta(1, "PV1"))
                        .puntoB(new PuntoVenta(2, "PV2"))
                        .build(),
                20
        );

        doThrow(new ConflictException("El camino ya existe"))
                .when(service).cargarCosto(any(CaminoPK.class), eq(20));

        mockMvc.perform(post("/costos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El camino ya existe"));
    }

    @Test
    void cuandoSeIntentaEliminarCaminoInexistente_retornaNotFound() throws Exception {
        CaminoPKRequest request = CaminoPKRequest.builder()
                .puntoA(new PuntoVenta(1, "PV1"))
                .puntoB(new PuntoVenta(2, "PV2"))
                .build();

        doThrow(NotFoundException.build("El camino no existe"))
                .when(service).removerCosto(any());

        mockMvc.perform(delete("/costos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("El camino no existe"));
    }

    @Test
    void cuandoServicioExternoFallaConRespuesta_retornaCodigoOriginal() throws Exception {
        CaminoRequest request = new CaminoRequest(
                CaminoPK.builder()
                        .puntoA(new PuntoVenta(1, "PV1"))
                        .puntoB(new PuntoVenta(2, "PV2"))
                        .build(),
                10
        );

        WebClientResponseException ex = WebClientResponseException.create(
                503, "Servicio no disponible", null, "Downstream error".getBytes(), null);

        doThrow(ex).when(service).cargarCosto(any(), anyInt());

        mockMvc.perform(post("/costos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Downstream error"));
    }

    @Test
    void cuandoServicioExternoFallaSinRespuesta_retornaInternalError() throws Exception {
        CaminoRequest request = new CaminoRequest(
                CaminoPK.builder()
                        .puntoA(new PuntoVenta(1, "PV1"))
                        .puntoB(new PuntoVenta(2, "PV2"))
                        .build(),
                10
        );
        doThrow(new WebClientException("Falla genérica") {}).when(service).cargarCosto(any(), anyInt());

        mockMvc.perform(post("/costos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error general al comunicarse con otro servicio: Falla genérica"));
    }

    @Test
    void cuandoOcurreExcepcionGenerica_retornaInternalServerError() throws Exception {
        when(service.consultarCaminos()).thenThrow(new RuntimeException("Algo falló"));

        mockMvc.perform(get("/costos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Algo falló"));
    }

    @Test
        void cuandoPuntoVentaNoExiste_retornaNotFoundDesdeServicioExterno() throws Exception {
        CaminoRequest request = new CaminoRequest(
                CaminoPK.builder()
                        .puntoA(new PuntoVenta(1, "PV1"))
                        .puntoB(new PuntoVenta(2, "PV2"))
                        .build(),
                10
        );

        WebClientResponseException ex = WebClientResponseException.create(
                404, "Not Found", HttpHeaders.EMPTY, "Punto de venta no encontrado".getBytes(), null);

        doThrow(ex).when(service).cargarCosto(any(), anyInt());

        mockMvc.perform(post("/costos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Punto de venta no encontrado"));
        }
}

