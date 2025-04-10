package com.federico.negocio.app.msvc_acreditaciones.services;


import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionRequest;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionResponse;

public interface AcreditacionService {
    
    AcreditacionResponse getAcreditacionById(String id);
    AcreditacionResponse guardarAcreditacion(AcreditacionRequest acreditacionRequest);
}
