package com.federico.negocio.app.msvc_puntos_costos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoPKRequest;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoRequest;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.ResultadoCamino;
import com.federico.negocio.app.msvc_puntos_costos.services.CostosService;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;



@WebMvcTest(CostosController.class)
class CostosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CostosService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void consultarCostos_deberiaRetornarListaDeCaminos() throws Exception {
        CaminoPK pk = CaminoPK.builder()
                .puntoA(new PuntoVenta(1, "PV1"))
                .puntoB(new PuntoVenta(2, "PV2"))
                .build();

        Camino camino = Camino.builder()
                .caminoPK(pk)
                .costo(10)
                .build();

        when(service.consultarCaminos()).thenReturn(List.of(camino));

        mockMvc.perform(get("/costos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].costo").value(10));
    }

    @Test
    void cargarCosto_deberiaRetornarCreated() throws Exception {
        CaminoRequest request = new CaminoRequest(
                CaminoPK.builder()
                        .puntoA(new PuntoVenta(1, "PV1"))
                        .puntoB(new PuntoVenta(2, "PV2"))
                        .build(),
                20
        );

        mockMvc.perform(post("/costos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(service).cargarCosto(any(CaminoPK.class), eq(20));
    }

    @Test
    void removerCosto_deberiaRetornarNoContent() throws Exception {
        CaminoPKRequest request = CaminoPKRequest.builder()
                .puntoA(new PuntoVenta(1, "PV1"))
                .puntoB(new PuntoVenta(2, "PV2"))
                .build();

        mockMvc.perform(delete("/costos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(service).removerCosto(eq(request));
    }

    @Test
    void consultarPuntosVenta_deberiaRetornarMapa() throws Exception {
        Map<Long, Integer> response = Map.of(2L, 15, 3L, 30);
        when(service.consultarPuntoventa(1)).thenReturn(response);

        mockMvc.perform(get("/costos/puntosVenta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.2").value(15))
                .andExpect(jsonPath("$.3").value(30));
    }

    @Test
    void consultarCostoMinimo_deberiaRetornarResultadoCamino() throws Exception {
        CaminoPKRequest request = CaminoPKRequest.builder()
                .puntoA(new PuntoVenta(1, "PV1"))
                .puntoB(new PuntoVenta(3, "PV3"))
                .build();

        ResultadoCamino resultado = new ResultadoCamino(25, List.of("PV1", "PV2", "PV3"));

        when(service.consultarCostoMinimo(eq(request))).thenReturn(resultado);

        mockMvc.perform(get("/costos/costoMinimo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costoTotal").value(25))
                .andExpect(jsonPath("$.camino[0]").value("PV1"))
                .andExpect(jsonPath("$.camino[2]").value("PV3"));
    }
}
