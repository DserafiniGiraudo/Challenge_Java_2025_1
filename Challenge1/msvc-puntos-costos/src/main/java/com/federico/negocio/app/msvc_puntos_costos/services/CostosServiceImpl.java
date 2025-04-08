package com.federico.negocio.app.msvc_puntos_costos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoFinder;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoFinder.ResultadoCamino;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoPKRequest;
import com.federico.negocio.app.msvc_puntos_costos.services.client.PuntoVentaClient;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CostosServiceImpl implements CostosService {

    HashMap<CaminoPK,Camino> caminos = new HashMap<CaminoPK,Camino>();

    private final PuntoVentaClient puntoVentaClient;
    private final CaminoFinder caminoFinder;

    private boolean initialized = false;

    private synchronized void init() {

        if (!caminos.isEmpty() || initialized) {
            return;
        }
        initialized = true;
        try {
            List <PuntoVenta> puntosVentas = puntoVentaClient.findAll();
            Map<Integer, PuntoVenta> mapaPuntos = puntosVentas
                    .stream()
                    .collect(Collectors.toMap(PuntoVenta::getId, Function.identity()));
            List<Camino> caminosList = new ArrayList<>();
    
            caminosList.add(crearCamino(mapaPuntos, 1, 2, 2));
            caminosList.add(crearCamino(mapaPuntos, 1, 3, 3));
            caminosList.add(crearCamino(mapaPuntos, 2, 3, 5));
            caminosList.add(crearCamino(mapaPuntos, 2, 4, 10));
            caminosList.add(crearCamino(mapaPuntos, 1, 4, 11));
            caminosList.add(crearCamino(mapaPuntos, 4, 5, 5));
            caminosList.add(crearCamino(mapaPuntos, 2, 5, 10));
            caminosList.add(crearCamino(mapaPuntos, 6, 7, 32));
            caminosList.add(crearCamino(mapaPuntos, 8, 9, 11));
            caminosList.add(crearCamino(mapaPuntos, 10, 7, 5));
            caminosList.add(crearCamino(mapaPuntos, 3, 8, 10));
            caminosList.add(crearCamino(mapaPuntos, 5, 8, 10));
            caminosList.add(crearCamino(mapaPuntos, 10, 5, 5));
            caminosList.add(crearCamino(mapaPuntos, 4, 6, 6));
    
            caminosList.forEach(c -> caminos.put(c.getCaminoPK(), c));
            caminosList.forEach(c -> caminos.put(c.caminoInverso().getCaminoPK(), c.caminoInverso()));
        } catch (Exception e) {
            throw e;
        }
    }

    private Map<CaminoPK, Camino> getCaminos() {
        if (caminos.isEmpty()) {
            init();
        }
        return caminos;
    }

    @Override
    @Cacheable(value = "caminos")
    public List<Camino> consultarCaminos() {
        return getCaminos().values().stream().collect(Collectors.toList());
    }

    @Override
    public void cargarCosto(CaminoPK caminoPK,int costo) {
        if (getCaminos().keySet().stream()
                .anyMatch(cpk -> 
                (cpk.getPuntoA().equals(caminoPK.getPuntoA()) && cpk.getPuntoB().equals(caminoPK.getPuntoB())) ||
                (cpk.getPuntoA().equals(caminoPK.getPuntoB()) && cpk.getPuntoB().equals(caminoPK.getPuntoA())))) {
            throw new ConflictException("El camino ya existe");
        }
        Camino camino = Camino.builder()
            .caminoPK(caminoPK)
            .costo(costo)
            .build();
        caminos.put(caminoPK, camino);
        Camino caminoInverso = camino.caminoInverso();
        caminos.put(caminoInverso.getCaminoPK(), caminoInverso);
    }

    @Override
    public void removerCosto(CaminoPKRequest caminoRequest) {
        if (getCaminos().keySet().stream()
                .noneMatch(cpk -> 
                (cpk.getPuntoA().equals(caminoRequest.getPuntoA()) && cpk.getPuntoB().equals(caminoRequest.getPuntoB())) ||
                (cpk.getPuntoA().equals(caminoRequest.getPuntoB()) && cpk.getPuntoB().equals(caminoRequest.getPuntoA())))) {
            throw new NotFoundException("El camino no existe");
        }
        CaminoPK caminoPK = CaminoPK.builder()
            .puntoA(caminoRequest.getPuntoA())
            .puntoB(caminoRequest.getPuntoB())
            .build();
        Camino camino = caminos.remove(caminoPK);
        caminos.remove(camino.caminoInverso().getCaminoPK());
    }

    @Override
    @Cacheable(value = "puntosVenta", key = "#puntoA")
    public Map<Long,Integer> consultarPuntoventa(Integer puntoA) {
        return getCaminos().values().stream()
            .filter(camino -> camino.getCaminoPK().getPuntoA().getId().equals(puntoA))
            .collect(Collectors.toMap(
                camino -> camino.getCaminoPK().getPuntoB().getId().longValue(),
                Camino::getCosto
            ));  
    }

    @Override
    public ResultadoCamino consultarCostoMinimo(CaminoPKRequest caminoRequest) {
        return caminoFinder.caminoMinimo(getCaminos().values().stream().toList(), caminoRequest);
    }

    private Camino crearCamino(Map<Integer, PuntoVenta> mapaPuntos, int idA, int idB, int costo) {
        CaminoPK pk = CaminoPK.builder()
                .puntoA(mapaPuntos.get(idA))
                .puntoB(mapaPuntos.get(idB))
                .build();
        return Camino.builder()
                .caminoPK(pk)
                .costo(costo)
                .build();
    }
}