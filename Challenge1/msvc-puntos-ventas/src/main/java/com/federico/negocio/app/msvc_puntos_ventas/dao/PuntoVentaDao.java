package com.federico.negocio.app.msvc_puntos_ventas.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;

@Repository
public interface PuntoVentaDao extends CrudRepository<PuntoVenta, Integer>{}
