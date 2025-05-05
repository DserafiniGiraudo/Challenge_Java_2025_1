package com.federico.negocio.app.msvc_puntos_costos.services.client;

import java.util.List;

import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;

public interface PuntoVentaClient {
    
    List<PuntoVenta> findAll();
    PuntoVenta findById(Integer id);

}
