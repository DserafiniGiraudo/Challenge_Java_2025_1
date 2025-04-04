package com.federico.negocio.app.msvc_puntos_costos.services;

import java.util.List;
import java.util.Map;

import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;

public interface CostosService {
    
    List<Camino> consultarCaminos();
    void cargarCosto(CaminoPK caminoPK,int costo);
    void removerCosto(Long puntoA,Long PuntoB);
    Map<Long,Integer> consultarPuntoventa(Long puntoA);
    int consultarCostoMinimo(Long puntoA,Long PuntoB);
}
