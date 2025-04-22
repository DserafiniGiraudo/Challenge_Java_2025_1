package com.federico.negocio.app.msvc_acreditaciones.mapper;

import org.mapstruct.Mapper;

import com.federico.negocio.app.msvc_acreditaciones.domain.Acreditacion;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionRequest;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionResponse;


@Mapper(componentModel = "spring")
public interface AcreditacionesMapper {

    Acreditacion toAcreditacion(AcreditacionRequest acreditacionRequest);    
    AcreditacionResponse toAcreditacionResponse(Acreditacion acreditacion);
}
