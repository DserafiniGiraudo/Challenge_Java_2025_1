package com.federico.negocio.app.msvc_puntos_ventas;

import com.federico.negocio.app.msvc_puntos_ventas.controller.PuntoVentaController;
import com.federico.negocio.app.msvc_puntos_ventas.services.PuntoVentaService;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PuntoVentaControllerTest {

    @Mock
    private PuntoVentaService service;

    @InjectMocks
    private static PuntoVentaController controller;

    private static MockMvc mockMvc;
    private static ObjectMapper objectMapper;
    private static List<PuntoVenta> puntoVentas;

    @BeforeAll
    static void beforeAll() {
        
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        puntoVentas = Arrays.asList(
                new PuntoVenta(1, "CABA"),
                new PuntoVenta(2, "GBA1"),
                new PuntoVenta(3, "GBA_2"),
                new PuntoVenta(4, "Santa Fe"),
                new PuntoVenta(5, "Cordoba"),
                new PuntoVenta(6, "Misiones"),
                new PuntoVenta(7, "Salta"),
                new PuntoVenta(8, "Chubut"),
                new PuntoVenta(9, "Santa Cruz"),
                new PuntoVenta(10, "Catamarca")
        );
    }

    @Nested
    class ObtenerPuntosVenta {

        @Test
        void findAll_deberiaRetornarListaDePuntosVenta() throws Exception {
            when(service.findAll()).thenReturn(puntoVentas);

            mockMvc.perform(get("/puntosVentas"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(1));

            verify(service, times(1)).findAll();
        }

        @Test
        void findById_deberiaRetornarPuntoVenta() throws Exception {
            when(service.findById(1)).thenReturn(puntoVentas.get(0)); // Usando el primer puntoVenta de la lista

            mockMvc.perform(get("/puntosVentas/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1));

            verify(service, times(1)).findById(1);
        }
    }

    @Nested
    class CrearPuntoVenta {

        @Test
        void save_deberiaRetornarPuntoVentaCreado() throws Exception {
            when(service.save(any(PuntoVenta.class))).thenReturn(puntoVentas.get(0)); // Usando el primer puntoVenta

            mockMvc.perform(post("/puntosVentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(puntoVentas.get(0))))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1));

            verify(service, times(1)).save(any(PuntoVenta.class));
        }
    }

    @Nested
    class ActualizarPuntoVenta {

        @Test
        void update_deberiaRetornarPuntoVentaActualizado() throws Exception {
            when(service.update(any(PuntoVenta.class), eq(1))).thenReturn(puntoVentas.get(0)); // Usando el primer puntoVenta

            mockMvc.perform(put("/puntosVentas/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(puntoVentas.get(0))))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1));

            verify(service, times(1)).update(any(PuntoVenta.class), eq(1));
        }
    }

    @Nested
    class EliminarPuntoVenta {

        @Test
        void delete_deberiaRetornarNoContent() throws Exception {
            mockMvc.perform(delete("/puntosVentas/1"))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).delete(1);
        }
    }
}