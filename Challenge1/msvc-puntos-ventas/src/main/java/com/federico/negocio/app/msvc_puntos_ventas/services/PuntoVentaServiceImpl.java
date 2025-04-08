package com.federico.negocio.app.msvc_puntos_ventas.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.federico.negocio.app.msvc_puntos_ventas.dao.PuntoVentaDao;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PuntoVentaServiceImpl implements PuntoVentaService {

    private final PuntoVentaDao puntoVentaDao;

    @PostConstruct
    private void poblarPuntosVenta() {
        List<PuntoVenta> puntosVenta = Arrays.asList(
                new PuntoVenta(1, "CABA"),
                new PuntoVenta(2, "GBA1"),
                new PuntoVenta(3, "GBA_2"),
                new PuntoVenta(4, "Santa Fe"),
                new PuntoVenta(5, "Cordoba"),
                new PuntoVenta(6, "Misiones"),
                new PuntoVenta(7, "Salta"),
                new PuntoVenta(8, "Chubut"),
                new PuntoVenta(9, "Santa Cruz"),
                new PuntoVenta(10, "Catamarca")
        );

        if(puntoVentaDao.count() == 0){
            puntoVentaDao.saveAll(puntosVenta);
        }
    }

    @Override
    @Cacheable(value = "puntosVenta", key = "#id")
    public PuntoVenta findById(int id) {
       return puntoVentaDao
            .findById(id)
            .orElseThrow(() -> new NotFoundException("PuntoVenta no encontrado con ID: " + id));
    }

    @Override
    @Cacheable(value = "puntosVentaList")
    public List<PuntoVenta> findAll() {
        return StreamSupport
            .stream(puntoVentaDao.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    @Override
    public PuntoVenta save(PuntoVenta puntoVenta) {
        return puntoVentaDao.save(puntoVenta);
    }

    @Override
    @CacheEvict(value = {"puntosVenta", "puntosVentaList"}, allEntries = true, key = "#puntoVenta.id")
    public PuntoVenta update(PuntoVenta puntoVenta, int id) {
        return puntoVentaDao
        .findById(id)
        .map(existingPuntoVenta -> {
            existingPuntoVenta.setPuntoVenta(puntoVenta.getPuntoVenta());
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