package com.federico.negocio.app.msvc_puntos_ventas.services;

import com.federico.negocio.app.msvc_puntos_ventas.dao.PuntoVentaDao;
import com.federico.negocio.app.msvc_puntos_ventas.domain.requests.PuntoVentaRequest;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PuntoVentaServiceImplTest")
class PuntoVentaServiceImplTest {

    @Mock
    private PuntoVentaDao puntoVentaDao;

    @InjectMocks
    private PuntoVentaServiceImpl service;

    private PuntoVenta puntoVenta1;
    private PuntoVenta puntoVenta2;
    private PuntoVentaRequest puntoVentaRequest1;
    private PuntoVentaRequest puntoVentaRequest2;

    @BeforeEach
    void setUp() {
        puntoVenta1 = new PuntoVenta();
        puntoVenta1.setId(1);
        puntoVenta1.setPuntoVenta("CABA");

        puntoVenta2 = new PuntoVenta();
        puntoVenta2.setId(2);
        puntoVenta2.setPuntoVenta("GBA1");

        puntoVentaRequest1 = new PuntoVentaRequest("Rosario");
        puntoVentaRequest2 = new PuntoVentaRequest("Córdoba");
    }

    @Test
    @DisplayName("findById - Debe retornar un PuntoVenta cuando el ID existe")
    void findById_existingId() {
        when(puntoVentaDao.findById(1)).thenReturn(Optional.of(puntoVenta1));
        PuntoVenta foundPuntoVenta = service.findById(1);
        assertNotNull(foundPuntoVenta);
        assertEquals(puntoVenta1.getId(), foundPuntoVenta.getId());
        assertEquals(puntoVenta1.getPuntoVenta(), foundPuntoVenta.getPuntoVenta());
        verify(puntoVentaDao, times(1)).findById(1);
    }

    @Test
    @DisplayName("findById - Debe lanzar NotFoundException cuando el ID no existe")
    void findById_nonExistingId() {
        when(puntoVentaDao.findById(3)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById(3));
        verify(puntoVentaDao, times(1)).findById(3);
    }

    @Test
    @DisplayName("findAll - Debe retornar una lista de todos los PuntosVenta")
    void findAll() {
        List<PuntoVenta> puntosVenta = Arrays.asList(puntoVenta1, puntoVenta2);
        when(puntoVentaDao.findAll()).thenReturn(puntosVenta);
        List<PuntoVenta> allPuntosVenta = service.findAll();
        assertNotNull(allPuntosVenta);
        assertEquals(2, allPuntosVenta.size());
        assertEquals(puntosVenta, allPuntosVenta);
        verify(puntoVentaDao, times(1)).findAll();
    }

    @Test
    @DisplayName("save - Debe guardar un nuevo PuntoVenta y retornar el PuntoVenta guardado con ID")
    void save() {
        PuntoVenta puntoVentaToSave = new PuntoVenta();
        puntoVentaToSave.setPuntoVenta(puntoVentaRequest1.puntoVenta());
        PuntoVenta savedPuntoVenta = new PuntoVenta();
        savedPuntoVenta.setId(3); // Simula el ID generado
        savedPuntoVenta.setPuntoVenta(puntoVentaRequest1.puntoVenta());

        when(puntoVentaDao.save(any(PuntoVenta.class))).thenReturn(savedPuntoVenta);

        PuntoVenta result = service.save(puntoVentaRequest1);
        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals(puntoVentaRequest1.puntoVenta(), result.getPuntoVenta());
        verify(puntoVentaDao, times(1)).save(any(PuntoVenta.class));
    }

    @Test
    @DisplayName("update - Debe actualizar un PuntoVenta existente y retornar el PuntoVenta actualizado")
    void update_existingId() {
        when(puntoVentaDao.findById(1)).thenReturn(Optional.of(puntoVenta1));
        PuntoVenta updatedPuntoVenta = new PuntoVenta();
        updatedPuntoVenta.setId(1);
        updatedPuntoVenta.setPuntoVenta(puntoVentaRequest2.puntoVenta());
        when(puntoVentaDao.save(any(PuntoVenta.class))).thenReturn(updatedPuntoVenta);

        PuntoVenta result = service.update(puntoVentaRequest2, 1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(puntoVentaRequest2.puntoVenta(), result.getPuntoVenta());
        verify(puntoVentaDao, times(1)).findById(1);
        verify(puntoVentaDao, times(1)).save(any(PuntoVenta.class));
    }

    @Test
    @DisplayName("update - Debe lanzar NotFoundException si el ID a actualizar no existe")
    void update_nonExistingId() {
        when(puntoVentaDao.findById(3)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.update(puntoVentaRequest2, 3));
        verify(puntoVentaDao, times(1)).findById(3);
        verify(puntoVentaDao, times(0)).save(any(PuntoVenta.class));
    }

    @Test
    @DisplayName("delete - Debe eliminar un PuntoVenta por ID")
    void delete() {
        doNothing().when(puntoVentaDao).deleteById(1);
        service.delete(1);
        verify(puntoVentaDao, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("findNameById - Debe retornar el nombre del PuntoVenta cuando el ID existe")
    void findNameById_existingId() {
        when(puntoVentaDao.findById(1)).thenReturn(Optional.of(puntoVenta1));
        String name = service.findNameById(1);
        assertEquals(puntoVenta1.getPuntoVenta(), name);
        verify(puntoVentaDao, times(1)).findById(1);
    }

    @Test
    @DisplayName("findNameById - Debe lanzar NotFoundException cuando el ID no existe")
    void findNameById_nonExistingId() {
        when(puntoVentaDao.findById(3)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findNameById(3));
        verify(puntoVentaDao, times(1)).findById(3);
    }
}