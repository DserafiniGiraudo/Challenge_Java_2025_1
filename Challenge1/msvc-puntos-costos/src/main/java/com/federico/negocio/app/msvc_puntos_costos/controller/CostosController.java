package com.federico.negocio.app.msvc_puntos_costos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoRequest;
import com.federico.negocio.app.msvc_puntos_costos.services.CostosService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/costos")
@RequiredArgsConstructor
public class CostosController {

    private final CostosService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Camino> consultarCostos() {
        return service.consultarCaminos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cargarCosto(@RequestBody CaminoRequest caminoRequest) {
        service.cargarCosto(caminoRequest.getCaminoPK(), caminoRequest.getCosto());
    }

    @DeleteMapping("/{id1}/{id2}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerCosto(@PathVariable Long id1,@PathVariable Long id2) {
        service.removerCosto(id1, id2);
    }

    @GetMapping("/puntosVenta/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Map<Long,Integer> consultarPuntosVenta(@PathVariable Long id) {
        return service.consultarPuntoventa(id);
    }

    @GetMapping("/costoMinimo/{id1}/{id2}")
    @ResponseStatus(HttpStatus.OK)
    public int consultarCostoMinimo(@PathVariable Long id1,@PathVariable Long id2) {
        return service.consultarCostoMinimo(id1, id2);
    }
}
