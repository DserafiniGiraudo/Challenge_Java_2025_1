package com.federico.negocio.app.msvc_puntos_costos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoPKRequest;
import com.federico.negocio.app.msvc_puntos_costos.services.client.PuntoVentaClient;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CostosServiceImpl implements CostosService {

    HashMap<CaminoPK,Camino> caminos = new HashMap<CaminoPK,Camino>();

    private final PuntoVentaClient puntoVentaClient;

    @PostConstruct
    private void init() {
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
    }

    @Override
    public List<Camino> consultarCaminos() {
        return caminos.values().stream().collect(Collectors.toList());
    }

    @Override
    public void cargarCosto(CaminoPK caminoPK,int costo) {
        if (caminos.keySet().stream()
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
       
        if (caminos.keySet().stream()
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
    public Map<Long,Integer> consultarPuntoventa(Integer puntoA) {

        return caminos.values().stream()
            .filter(camino -> camino.getCaminoPK().getPuntoA().getId().equals(puntoA))
            .collect(Collectors.toMap(
                camino -> camino.getCaminoPK().getPuntoB().getId().longValue(),
                Camino::getCosto
            ));  
    }

    @Override
    public int consultarCostoMinimo(CaminoPKRequest caminoRequest) {

        throw new UnsupportedOperationException("Unimplemented method 'consultarCostoMinimo'");
        // boolean existePuntoB =caminos.keySet().stream()
        //     .anyMatch(cpk-> cpk.getPuntoB().equals(PuntoB));

        //     if(!existePuntoB){
        //         throw new RuntimeException("El punto B no existe");
        //     }

        // Optional<Camino> caminoDirectoOptional = caminos.values().stream()
        //     .filter(c -> c.getCaminoPK().getPuntoA().equals(puntoA) && c.getCaminoPK().getPuntoB().equals(PuntoB))
        //     .findFirst();
            
        //     if(caminoDirectoOptional.isPresent()){
        //         return caminoDirectoOptional.get().getCosto();
        //     }

            /* 
             * 1)Filtro todos los caminos que el origen sean el punto A.
             * 2)obtengo todos los puntos B de esos caminos
             * 3)Obtengo todos los puntos a los que van esos caminos
             * 4)Filtrar solo los destinos que coincidan con el punto B
             * 5)Obtengo el valor del punto B
            */

            // return caminos.values().stream()
            // .filter(camino-> camino.getCaminoPK().getPuntoA().equals(puntoA))
            // .map(camino-> camino.getCaminoPK().getPuntoB())
            // .map(this::consultarPuntoventa1)
            // .filter(puntoVenta-> puntoVenta.containsKey(PuntoB))
            // .map(puntoVenta-> puntoVenta.get(PuntoB))
            // .findFirst()
            // .orElse(0);

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
