package com.federico.negocio.app.msvc_puntos_ventas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.federico.negocio.app.msvc_puntos_ventas.domain.requests.PuntoVentaRequest;
import com.federico.negocio.app.msvc_puntos_ventas.services.PuntoVentaService;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/puntosVentas")
@RequiredArgsConstructor
public class PuntoVentaController {

    private final PuntoVentaService service;

    @GetMapping
    public List<PuntoVenta> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PuntoVenta findById(@PathVariable int id){
       return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PuntoVenta save(@RequestBody @Validated PuntoVentaRequest puntoVentaRequest) {
        return service.save(puntoVentaRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PuntoVenta update(@Validated @RequestBody PuntoVentaRequest puntoVenta, @PathVariable int id) {
        return service.update(puntoVenta, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}