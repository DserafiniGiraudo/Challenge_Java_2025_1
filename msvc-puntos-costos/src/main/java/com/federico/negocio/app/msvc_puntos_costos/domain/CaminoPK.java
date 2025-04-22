package com.federico.negocio.app.msvc_puntos_costos.domain;

import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CaminoPK {

    private PuntoVenta puntoA;
    private PuntoVenta puntoB;

}
