package com.federico.negocio.app.msvc_acreditaciones.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.federico.negocio.app.msvc_acreditaciones.domain.Acreditacion;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionRequest;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionResponse;
import com.federico.negocio.app.msvc_acreditaciones.mapper.AcreditacionesMapper;
import com.federico.negocio.app.msvc_acreditaciones.repositories.AcreditacionRepository;
import com.federico.negocio.app.msvc_acreditaciones.services.client.PuntoVentaClient;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcreditacionServiceImpl implements AcreditacionService {

    private final AcreditacionRepository repo;
    private final PuntoVentaClient puntoVentaClient;
    private final AcreditacionesMapper mapper;

    @Override
    @Transactional
    public AcreditacionResponse guardarAcreditacion(AcreditacionRequest acreditacionRequest) {
        
        String nombrePuntoventa = puntoVentaClient.findNameById(acreditacionRequest.identificadorPuntoVenta());
        Acreditacion acreditacion = mapper.toAcreditacion(acreditacionRequest);
        acreditacion.setIdentificadorPuntoVenta(acreditacionRequest.identificadorPuntoVenta());
        acreditacion.setNombrePuntoventa(nombrePuntoventa);
        acreditacion.setImporte(acreditacionRequest.importe());
        acreditacion.setFechaPedido(LocalDate.now());
        return mapper.toAcreditacionResponse(repo.save(acreditacion));
    }

    @Override
    public AcreditacionResponse getAcreditacionById(String id) {

        return repo.findById(id)
                .map(mapper::toAcreditacionResponse)
                .orElseThrow(() -> new NotFoundException("Acreditacion no encontrada con el id  " + id));
    }
}
