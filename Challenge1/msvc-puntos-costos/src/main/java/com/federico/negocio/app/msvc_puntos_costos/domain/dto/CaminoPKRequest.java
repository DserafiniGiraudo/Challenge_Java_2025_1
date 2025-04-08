package com.federico.negocio.app.msvc_puntos_costos.domain.dto;

import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CaminoPKRequest {

    @NotNull
    private PuntoVenta puntoA;
    @NotNull
    private PuntoVenta puntoB;

}
