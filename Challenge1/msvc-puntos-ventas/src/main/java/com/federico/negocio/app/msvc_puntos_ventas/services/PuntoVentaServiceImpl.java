package com.federico.negocio.app.msvc_puntos_ventas.services;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.federico.negocio.app.msvc_puntos_ventas.dao.PuntoVentaDao;
import com.federico.negocio.app.msvc_puntos_ventas.domain.requests.PuntoVentaRequest;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PuntoVentaServiceImpl implements PuntoVentaService {

    private final PuntoVentaDao puntoVentaDao;
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @PostConstruct
    private void poblarPuntosVenta() {
        if (puntoVentaDao.count() == 0) {
            List<PuntoVenta> puntosVenta = Arrays.asList(
                    convertirAPuntoVenta(new PuntoVentaRequest("CABA")),
                    convertirAPuntoVenta(new PuntoVentaRequest("GBA1")),
                    convertirAPuntoVenta(new PuntoVentaRequest("GBA_2")),
                    convertirAPuntoVenta(new PuntoVentaRequest("Santa Fe")),
                    convertirAPuntoVenta(new PuntoVentaRequest("Cordoba")),
                    convertirAPuntoVenta(new PuntoVentaRequest("Misiones")),
                    convertirAPuntoVenta(new PuntoVentaRequest("Salta")),
                    convertirAPuntoVenta(new PuntoVentaRequest("Chubut")),
                    convertirAPuntoVenta(new PuntoVentaRequest("Santa Cruz")),
                    convertirAPuntoVenta(new PuntoVentaRequest("Catamarca"))
            );
            puntoVentaDao.saveAll(puntosVenta);
        }
    }

    private PuntoVenta convertirAPuntoVenta(PuntoVentaRequest request) {
        PuntoVenta puntoVenta = new PuntoVenta();
        puntoVenta.setId(idGenerator.getAndIncrement());
        puntoVenta.setPuntoVenta(request.puntoVenta());
        return puntoVenta;
    }

    @Override
    @Cacheable(value = "puntosVenta", key = "#id")
    public PuntoVenta findById(int id) {
       return puntoVentaDao
            .findById(id)
            .orElseThrow(() -> new NotFoundException("PuntoVenta no encontrado con ID: " + id));
    }

    @Override
    // @Cacheable(value = "puntosVentaList")
    public List<PuntoVenta> findAll() {
        return StreamSupport
            .stream(puntoVentaDao.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "puntosVentaList", allEntries = true)
    public PuntoVenta save(PuntoVentaRequest puntoVenta) {

        PuntoVenta pv = PuntoVenta.builder()
            .id(idGenerator.incrementAndGet())
            .puntoVenta(puntoVenta.puntoVenta())
            .build();
        return puntoVentaDao.save(pv);
    }

    @Override
    @CacheEvict(value = {"puntosVenta", "puntosVentaList"}, allEntries = true, key = "#puntoVenta.id")
    public PuntoVenta update(PuntoVentaRequest puntoVenta, int id) {
        return puntoVentaDao
        .findById(id)
        .map(existingPuntoVenta -> {
            existingPuntoVenta.setPuntoVenta(puntoVenta.puntoVenta());
            return puntoVentaDao.save(existingPuntoVenta);
        })
        .orElseThrow(() -> new NotFoundException("PuntoVenta no encontrado con ID: " + id));
    }

    @Override
    @CacheEvict(value = {"puntosVenta", "puntosVentaList"}, allEntries = true, key = "#id")
    public void delete(int id) {
        puntoVentaDao.deleteById(id);
    }

    @PreDestroy
    private void eliminarPuntosVenta() {
        puntoVentaDao.deleteAll();
    }
}