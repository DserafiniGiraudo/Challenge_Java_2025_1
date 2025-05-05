package com.federico.negocio.app.msvc_puntos_ventas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.federico.negocio.app.msvc_puntos_ventas.domain.requests.PuntoVentaRequest;
import com.federico.negocio.app.msvc_puntos_ventas.services.PuntoVentaService;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("PuntoVentaControllerTest")
public class PuntoVentaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PuntoVentaService service;
    private static PuntoVenta puntoVenta1;
    private static PuntoVenta puntoVenta2;
    private static PuntoVenta puntoVenta3;
    private static PuntoVenta puntoVentaActualizado;
    private static PuntoVentaRequest puntoVentaRequest;
    private static List<PuntoVenta> puntosVenta;

    @BeforeAll
    static void setup() {
        puntoVenta1 = PuntoVenta.builder()
                .id(1)
                .puntoVenta("CABA")
                .build();

        puntoVenta2 = PuntoVenta.builder()
                .id(2)
                .puntoVenta("GBA1")
                .build();
        puntoVenta3 = PuntoVenta.builder()
                .id(3)
                .puntoVenta("GBA2")
                .build();
        puntosVenta = Arrays.asList(puntoVenta1, puntoVenta2);

        puntoVentaActualizado = PuntoVenta.builder()
                .id(1)
                .puntoVenta("Rosario")
                .build();

        puntoVentaRequest = new PuntoVentaRequest("Rosario");
    }

    @Test
    @DisplayName("DELETE /puntosVentas/{id} - Debe retornar No Content al eliminar")
    void testDelete() throws Exception {
        doNothing().when(service).delete(1);
        mockMvc.perform(delete("/puntosVentas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /puntosVentas - Debe retornar Ok y la lista de puntos de venta")
    void testFindAll() throws Exception {
        when(service.findAll()).thenReturn(puntosVenta);

        mockMvc.perform(get("/puntosVentas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].puntoVenta", is("CABA")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].puntoVenta", is("GBA1")));
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /puntosVentas/{id} - Debe retornar Ok y el punto de venta si existe")
    void testFindById_ExistingId() throws Exception {
        when(service.findById(1)).thenReturn(puntoVenta1);

        mockMvc.perform(get("/puntosVentas/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.puntoVenta", is("CABA")));
        verify(service, times(1)).findById(1);
    }

    @Test
    @DisplayName("GET /puntosVentas/{id} - Debe retornar Not Found si el punto de venta no existe")
    void testFindById_NonExistingId() throws Exception {
        when(service.findById(3)).thenThrow(NotFoundException.build("PuntoVenta no encontrado con ID: 3"));

        mockMvc.perform(get("/puntosVentas/3"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("PuntoVenta no encontrado con ID: 3")));
        verify(service, times(1)).findById(3);
    }

    @Test
    @DisplayName("GET /puntosVentas/nombres/{id} - Debe retornar Ok y el nombre si existe")
    void testFindNameById_existingId() throws Exception {
        when(service.findNameById(1)).thenReturn("CABA");

        mockMvc.perform(get("/puntosVentas/nombres/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN + ";charset=UTF-8"))
                .andExpect(content().string("CABA"));
        verify(service, times(1)).findNameById(1);
    }

    @Test
    @DisplayName("GET /puntosVentas/nombres/{id} - Debe retornar Not Found si no existe")
    void testFindNameById_nonExistingId() throws Exception {
        when(service.findNameById(3)).thenThrow(NotFoundException.build("PuntoVenta no encontrado con ID: 3"));

        mockMvc.perform(get("/puntosVentas/nombres/3"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("PuntoVenta no encontrado con ID: 3")));
        verify(service, times(1)).findNameById(3);
    }

    @Test
    @DisplayName("POST /puntosVentas - Debe retornar Created y el punto de venta guardado")
    void testSave_validInput() throws Exception {
        when(service.save(any(PuntoVentaRequest.class))).thenReturn(puntoVenta3);

        mockMvc.perform(post("/puntosVentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(puntoVentaRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)));
        verify(service, times(1)).save(any(PuntoVentaRequest.class));
    }

    @Test
    @DisplayName("PUT /puntosVentas/{id} - Debe retornar Ok y el punto de venta actualizado si existe")
    void testUpdate_existingId() throws Exception {
        when(service.update(puntoVentaRequest, 1)).thenReturn(puntoVentaActualizado);
        mockMvc.perform(put("/puntosVentas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(puntoVentaRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.puntoVenta", is("Rosario")));
        verify(service, times(1)).update(any(PuntoVentaRequest.class), eq(1));
    }

    @Test
    @DisplayName("PUT /puntosVentas/{id} - Debe retornar Not Found si el punto de venta a actualizar no existe")
    void testUpdate_nonExistingId() throws Exception {
        when(service.update(puntoVentaRequest, 3)).thenThrow(NotFoundException.build("PuntoVenta no encontrado con ID: 3"));
        mockMvc.perform(put("/puntosVentas/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(puntoVentaRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("PuntoVenta no encontrado con ID: 3")));
        verify(service, times(1)).update(any(PuntoVentaRequest.class), eq(3));
    }

    @Test
    @DisplayName("PUT /puntosVentas/{id} - Debe retornar Bad Request si el body esta mal")
    void testUpdate_InvalidBody() throws Exception {
        PuntoVentaRequest invalidRequest = new PuntoVentaRequest("");

        mockMvc.perform(put("/puntosVentas/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.puntoVenta", is("El campo puntoVenta no puede estar vacío")));
        verify(service, times(0)).update(any(PuntoVentaRequest.class), eq(0));
    }
}
