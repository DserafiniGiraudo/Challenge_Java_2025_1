package com.federico.negocio.app.msvc_puntos_costos.services;

import java.util.List;
import java.util.Map;

import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoPKRequest;

public interface CostosService {
    
    List<Camino> consultarCaminos();
    void cargarCosto(CaminoPK caminoPK,int costo);
    void removerCosto(CaminoPKRequest camino);
    Map<Long,Integer> consultarPuntoventa(Integer puntoA);
    int consultarCostoMinimo(CaminoPKRequest caminoRequest);
}
