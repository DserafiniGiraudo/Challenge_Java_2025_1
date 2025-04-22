package com.federico.negocio.app.msvc_puntos_costos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoFinder;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoPKRequest;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.ResultadoCamino;
import com.federico.negocio.app.msvc_puntos_costos.services.CostosServiceImpl;
import com.federico.negocio.app.msvc_puntos_costos.services.client.PuntoVentaClient;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.ConflictException;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class CostosServiceImplTest {

    @Mock
    private PuntoVentaClient puntoVentaClient;

    @Mock
    private CaminoFinder caminoFinder;

    @InjectMocks
    private CostosServiceImpl costosService;

    private List<PuntoVenta> puntosDeVenta;

    @BeforeEach
    void setUp() {
        // Creamos 10 puntos de venta
        puntosDeVenta = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> PuntoVenta.builder()
                        .id(i)
                        .puntoVenta("Sucursal " + i)
                        .build())
                .collect(Collectors.toList());

        when(puntoVentaClient.findAll()).thenReturn(puntosDeVenta);
    }

    @Test
    void consultarCaminos_deberiaInicializarYRetornar28Caminos() {
        List<Camino> caminos = costosService.consultarCaminos();
        assertEquals(28, caminos.size()); // 14 caminos + 14 inversos
    }

    @Test
    void cargarCosto_nuevoCamino_deberiaAgregarlo() {
        // Arrange
        CaminoPK caminoPK = CaminoPK.builder()
                .puntoA(puntosDeVenta.get(0)) // id 1
                .puntoB(puntosDeVenta.get(9)) // id 10
                .build();

        when(puntoVentaClient.findById(1)).thenReturn(puntosDeVenta.get(0));
        when(puntoVentaClient.findById(10)).thenReturn(puntosDeVenta.get(9));

        // Act
        costosService.cargarCosto(caminoPK, 99);

        // Assert
        List<Camino> caminos = costosService.consultarCaminos();
        boolean existe = caminos.stream().anyMatch(c ->
                (c.getCaminoPK().getPuntoA().getId() == 1 && c.getCaminoPK().getPuntoB().getId() == 10) ||
                (c.getCaminoPK().getPuntoA().getId() == 10 && c.getCaminoPK().getPuntoB().getId() == 1)
        );
        assertTrue(existe);
    }

    @Test
    void cargarCosto_caminoExistente_deberiaLanzarException() {
        // Primero forzamos la inicialización de caminos
        costosService.consultarCaminos();

        CaminoPK caminoPKExistente = CaminoPK.builder()
                .puntoA(puntosDeVenta.get(0)) // id 1
                .puntoB(puntosDeVenta.get(1)) // id 2
                .build();

        ConflictException ex = assertThrows(ConflictException.class, () ->
                costosService.cargarCosto(caminoPKExistente, 10)
        );

        assertEquals("El camino ya existe", ex.getMessage());
    }

    @Test
    void removerCosto_caminoExistente_deberiaEliminarlo() {
        // Arrange
        CaminoPK nuevoCamino = CaminoPK.builder()
                .puntoA(puntosDeVenta.get(2)) // id 3
                .puntoB(puntosDeVenta.get(4)) // id 5
                .build();

        when(puntoVentaClient.findById(3)).thenReturn(puntosDeVenta.get(2));
        when(puntoVentaClient.findById(5)).thenReturn(puntosDeVenta.get(4));

        costosService.cargarCosto(nuevoCamino, 77);

        // Act
        CaminoPKRequest req = CaminoPKRequest.builder()
                .puntoA(puntosDeVenta.get(2))
                .puntoB(puntosDeVenta.get(4))
                .build();

        costosService.removerCosto(req);

        // Assert
        List<Camino> caminos = costosService.consultarCaminos();
        boolean existe = caminos.stream().anyMatch(c ->
                (c.getCaminoPK().getPuntoA().getId() == 3 && c.getCaminoPK().getPuntoB().getId() == 5) ||
                (c.getCaminoPK().getPuntoA().getId() == 5 && c.getCaminoPK().getPuntoB().getId() == 3)
        );
        assertFalse(existe);
    }

    @Test
    void removerCosto_caminoInexistente_deberiaLanzarNotFound() {
        CaminoPKRequest req = CaminoPKRequest.builder()
                .puntoA(puntosDeVenta.get(6))
                .puntoB(puntosDeVenta.get(8))
                .build();

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            costosService.removerCosto(req);
        });

        assertEquals("El camino no existe", ex.getMessage());
    }

    @Test
    void consultarPuntoVenta_deberiaRetornarDestinosDesdeUnOrigen() {
        Map<Long, Integer> resultado = costosService.consultarPuntoventa(1);
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.containsKey(2L)); // porque existe camino de 1 → 2
    }

    @Test
    void consultarCostoMinimo_deberiaDelegarEnCaminoFinder() {
        CaminoPKRequest req = CaminoPKRequest.builder()
                .puntoA(puntosDeVenta.get(0)) // id 1
                .puntoB(puntosDeVenta.get(3)) // id 4
                .build();

        ResultadoCamino esperado = new ResultadoCamino(11, List.of("Sucursal 1", "Sucursal 4"));
        when(caminoFinder.caminoMinimo(anyList(), eq(req))).thenReturn(esperado);

        ResultadoCamino resultado = costosService.consultarCostoMinimo(req);
        assertEquals(esperado, resultado);
    }
}
