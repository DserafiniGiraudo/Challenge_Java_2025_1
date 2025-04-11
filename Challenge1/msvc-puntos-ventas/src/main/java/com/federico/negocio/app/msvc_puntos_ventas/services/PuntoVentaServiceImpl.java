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
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @PostConstruct
    private void inicializarIdGenerator() {
        StreamSupport.stream(puntoVentaDao.findAll().spliterator(), false)
                .map(PuntoVenta::getId)
                .max(Integer::compareTo)
                .ifPresent(maxId -> idGenerator.set(maxId));

        if (puntoVentaDao.count() == 0) {
            poblarPuntosVentaInicial();
        }
    }

    private void poblarPuntosVentaInicial() {
        List<PuntoVenta> puntosVenta = Arrays.asList(
                convertirAPuntoVentaInterno(new PuntoVentaRequest("CABA")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("GBA1")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("GBA_2")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("Santa Fe")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("Cordoba")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("Misiones")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("Salta")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("Chubut")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("Santa Cruz")),
                convertirAPuntoVentaInterno(new PuntoVentaRequest("Catamarca"))
        );
        puntoVentaDao.saveAll(puntosVenta);

        // Actualizar el idGenerator después de la población inicial
        puntosVenta.stream()
                .map(PuntoVenta::getId)
                .max(Integer::compareTo)
                .ifPresent(maxId -> idGenerator.set(Math.max(idGenerator.get(), maxId)));
    }

    private PuntoVenta convertirAPuntoVentaInterno(PuntoVentaRequest request) {
        PuntoVenta puntoVenta = new PuntoVenta();
        puntoVenta.setId(idGenerator.incrementAndGet());
        puntoVenta.setPuntoVenta(request.puntoVenta());
        return puntoVenta;
    }

    private PuntoVenta convertirAPuntoVenta(PuntoVentaRequest request) {
        PuntoVenta puntoVenta = new PuntoVenta();
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
    public List<PuntoVenta> findAll() {
        return StreamSupport
                .stream(puntoVentaDao.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public PuntoVenta save(PuntoVentaRequest puntoVenta) {
        PuntoVenta pv = convertirAPuntoVenta(puntoVenta);
        pv.setId(idGenerator.incrementAndGet());
        return puntoVentaDao.save(pv);
    }

    @Override
    @CacheEvict(value = {"puntosVenta"}, key = "#puntoVenta.id")
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
    @CacheEvict(value = {"puntosVenta"}, key = "#id")
    public void delete(int id) {
        puntoVentaDao.deleteById(id);
    }

    @PreDestroy
    private void eliminarPuntosVenta() {
        puntoVentaDao.deleteAll();
    }

    @Override
    public String findNameById(int id) {
        return puntoVentaDao
                .findById(id)
                .map(PuntoVenta::getPuntoVenta)
                .orElseThrow(() -> new NotFoundException("PuntoVenta no encontrado con ID: " + id));
    }
}