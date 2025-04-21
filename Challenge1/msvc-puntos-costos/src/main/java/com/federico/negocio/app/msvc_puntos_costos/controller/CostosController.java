package com.federico.negocio.app.msvc_puntos_costos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.federico.negocio.app.msvc_puntos_costos.domain.Camino;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoPKRequest;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoRequest;
import com.federico.negocio.app.msvc_puntos_costos.domain.dto.ResultadoCamino;
import com.federico.negocio.app.msvc_puntos_costos.services.CostosService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
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
    public void cargarCosto(@RequestBody @Validated CaminoRequest caminoRequest) {
        service.cargarCosto(caminoRequest.caminoPK(), caminoRequest.costo());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerCosto(@RequestBody @Validated CaminoPKRequest caminoRequest) {
        service.removerCosto(caminoRequest);
    }

    @GetMapping("/puntosVenta/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Map<Long,Integer> consultarPuntosVenta(@PathVariable Integer id) {
        return service.consultarPuntoventa(id);
    }

    @GetMapping("/costoMinimo")
    @ResponseStatus(HttpStatus.OK)
    public ResultadoCamino consultarCostoMinimo(@RequestBody @Validated CaminoPKRequest caminoRequest) {
        return service.consultarCostoMinimo(caminoRequest);
    }
}
