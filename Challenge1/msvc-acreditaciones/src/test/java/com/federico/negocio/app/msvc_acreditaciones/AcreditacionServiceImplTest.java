package com.federico.negocio.app.msvc_acreditaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.federico.negocio.app.msvc_acreditaciones.domain.Acreditacion;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionRequest;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionResponse;
import com.federico.negocio.app.msvc_acreditaciones.mapper.AcreditacionesMapper;
import com.federico.negocio.app.msvc_acreditaciones.repositories.AcreditacionRepository;
import com.federico.negocio.app.msvc_acreditaciones.services.AcreditacionServiceImpl;
import com.federico.negocio.app.msvc_acreditaciones.services.client.PuntoVentaClient;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcreditacionServiceImplTest")
class AcreditacionServiceImplTest {

    @Mock 
    AcreditacionRepository repo;
    @Mock
    PuntoVentaClient puntoVentaClient;
    @Mock
    AcreditacionesMapper mapper;

    @InjectMocks
    private AcreditacionServiceImpl service;

    @Test
    @DisplayName("guardarAcreditacion - Debe guardar una acreditacion correctamente")
    void guardarAcreditacion() {
        
        AcreditacionRequest request = new AcreditacionRequest(1, 1000.0);
        Acreditacion acreditacion = Acreditacion.builder()
            .id(UUID.randomUUID().toString())
            .identificadorPuntoVenta(1)
            .importe(1000.0)
            .fechaPedido(LocalDate.now())
            .nombrePuntoventa("CABA")
            .build();
        AcreditacionResponse expectedResponse = new AcreditacionResponse("1", 1, "CABA", 1000.0, LocalDate.now());

        when(puntoVentaClient.findNameById(request.identificadorPuntoVenta())).thenReturn("CABA");
        when(repo.save(any())).thenReturn(acreditacion);
        when(mapper.toAcreditacion(any())).thenReturn(acreditacion);
        when(mapper.toAcreditacionResponse(any())).thenReturn(expectedResponse);

        AcreditacionResponse response = service.guardarAcreditacion(request);
        assertEquals(expectedResponse, response);
    }

    @Test
    @DisplayName("getAcreditacionById - Debe retornar una acreditacion por ID")
    void getAcreditacionById() {
        String id = "1";
        Acreditacion acreditacion = Acreditacion.builder()
            .id(id)
            .identificadorPuntoVenta(1)
            .importe(1000.0)
            .fechaPedido(LocalDate.now())
            .nombrePuntoventa("CABA")
            .build();
        AcreditacionResponse expectedResponse = new AcreditacionResponse(id, 1, "CABA", 1000.0, LocalDate.now());

        when(repo.findById(id)).thenReturn(java.util.Optional.of(acreditacion));
        when(mapper.toAcreditacionResponse(acreditacion)).thenReturn(expectedResponse);

        AcreditacionResponse response = service.getAcreditacionById(id);
        assertEquals(expectedResponse, response);
    }

    @Test
    @DisplayName("getAcreditacionById - Debe lanzar NotFoundException cuando no se encuentra la acreditacion")
    void getAcreditacionById_notFound() {
        String id = "1";

        when(repo.findById(id)).thenReturn(java.util.Optional.empty());
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.getAcreditacionById(id);
        });

        assertEquals("Acreditacion no encontrada con el id  " + id, thrown.getMessage());
    }
}

