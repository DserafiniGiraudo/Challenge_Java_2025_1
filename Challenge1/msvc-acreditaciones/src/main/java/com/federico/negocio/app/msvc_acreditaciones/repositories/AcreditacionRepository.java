package com.federico.negocio.app.msvc_acreditaciones.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.federico.negocio.app.msvc_acreditaciones.domain.Acreditacion;

public interface AcreditacionRepository extends MongoRepository<Acreditacion, String> {
    List<Acreditacion> findByIdentificadorPuntoVenta(int identificadorPuntoVenta);
}
