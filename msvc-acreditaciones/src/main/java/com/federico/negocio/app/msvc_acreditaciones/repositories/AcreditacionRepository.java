package com.federico.negocio.app.msvc_acreditaciones.repositories;

import org.springframework.data.repository.ListCrudRepository;

import com.federico.negocio.app.msvc_acreditaciones.domain.Acreditacion;

public interface AcreditacionRepository extends ListCrudRepository<Acreditacion, String> {

}
