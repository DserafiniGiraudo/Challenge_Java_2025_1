package com.federico.negocio.app.msvc_puntos_ventas.services;

import java.util.List;

import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;


public interface PuntoVentaService {

    public PuntoVenta findById(int id);
    public List<PuntoVenta> findAll();
    public PuntoVenta save(PuntoVenta puntoVenta);
    public PuntoVenta update(PuntoVenta puntoVenta,int id);
    public void delete(int id);
}
