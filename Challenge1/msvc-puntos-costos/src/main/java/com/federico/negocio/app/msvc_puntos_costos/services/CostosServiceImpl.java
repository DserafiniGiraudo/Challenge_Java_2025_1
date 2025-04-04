package com.federico.negocio.app.msvc_puntos_costos.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CostosServiceImpl implements CostosService {

    HashMap<CaminoPK,Camino> caminos = new HashMap<CaminoPK,Camino>();

    @PostConstruct
    private void init() {
       
        Arrays.asList(
        new Camino( new CaminoPK(1L,2L),2),
        new Camino( new CaminoPK(1L,3L),3),
        new Camino( new CaminoPK(2L,3L),5),
        new Camino( new CaminoPK(2L,4L),10),
        new Camino( new CaminoPK(1L,4L),11),
        new Camino( new CaminoPK(4L,5L),5),
        new Camino( new CaminoPK(2L,5L),10),
        new Camino( new CaminoPK(6L,7L),32),
        new Camino( new CaminoPK(8L,9L),11),
        new Camino( new CaminoPK(10L,7L),5),
        new Camino( new CaminoPK(3L,8L),10),
        new Camino( new CaminoPK(5L,8L),10),
        new Camino( new CaminoPK(10L,5L),5),
        new Camino( new CaminoPK(4L,6L),6))
        .forEach(camino -> {
            caminos.put(camino.getCaminoPK(), camino);
            caminos.put(camino.caminoInverso().getCaminoPK(), camino.caminoInverso());
        });
    }

    public void cargarCosto(CaminoPK caminoPK,int costo) {
        
        if (caminos.keySet().stream()
                .anyMatch(cpk -> 
                (cpk.getPuntoA().equals(caminoPK.getPuntoA()) && cpk.getPuntoB().equals(caminoPK.getPuntoB())) ||
                (cpk.getPuntoA().equals(caminoPK.getPuntoB()) && cpk.getPuntoB().equals(caminoPK.getPuntoA())))) {
            throw new ConflictException("El camino ya existe");
        }

        Camino camino = new Camino(caminoPK, costo);
        caminos.put(caminoPK, camino);
        Camino caminoInverso = camino.caminoInverso();
        caminos.put(caminoInverso.getCaminoPK(), caminoInverso);
    }

    @Override
    public void removerCosto(Long puntoA, Long puntoB) {
        CaminoPK caminoPKDirecto = new CaminoPK(puntoA, puntoB);
        CaminoPK caminoPKInverso = new CaminoPK(puntoB, puntoA);

        caminos.keySet().removeIf(cpk -> cpk.equals(caminoPKDirecto) || cpk.equals(caminoPKInverso));
    }

    public Map<Long,Integer> consultarPuntoventa(Long puntoA) {

        return caminos.entrySet().stream()
            .filter(es -> es.getKey().getPuntoA().equals(puntoA))
            .findAny()
            .map(es -> caminos.entrySet().stream()
                    .filter(e -> e.getKey().getPuntoA().equals(puntoA))
                    .collect(Collectors.toMap(
                            e -> e.getKey().getPuntoB(),
                            e -> e.getValue().getCosto()
                    )))
            .orElseThrow(() -> new NotFoundException("No se encontraron caminos para el puntoA: " + puntoA));
    }

    @Override
    public int consultarCostoMinimo(Long puntoA, Long PuntoB) {

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
    


    @Override
    public List<Camino> consultarCaminos() {
        return caminos.values().stream().collect(Collectors.toList());
    }

}
