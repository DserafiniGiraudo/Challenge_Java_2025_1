package com.federico.negocio.app.msvc_acreditaciones;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.federico.negocio.app.msvc_acreditaciones.mapper.AcreditacionesMapper;
import com.federico.negocio.app.msvc_acreditaciones.repositories.AcreditacionRepository;
import com.federico.negocio.app.msvc_acreditaciones.services.AcreditacionServiceImpl;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class AcreditacionesExceptionHandlerTest {

    @Mock
    AcreditacionRepository repo;

    @Mock
    AcreditacionesMapper mapper;

    @InjectMocks
    AcreditacionServiceImpl acreditacionService;

    @Test
    @DisplayName("handleNotFoundException - Debe retornar un error 404 cuando no se encuentra el recurso")
    void handleNotFoundException() {
        
        when(repo.findById(anyString())).thenThrow(new NotFoundException("Acreditacion no encontrada con el id  " + "1"));
        assertThrows(NotFoundException.class, () -> {
            acreditacionService.getAcreditacionById("1");
        });

    }

    // @Test
    // @DisplayName("handleConflictException - Debe retornar un error 409 cuando hay un conflicto")
    // void handleConflictException() {
    //     // Simular un conflicto
    //     when(repo.existsById(anyString())).thenReturn(true);
    //     assertThrows(ConflictException.class, () -> {
    //         acreditacionService.createAcreditacion(new AcreditacionRequest());
    //     });
    // }

    
}