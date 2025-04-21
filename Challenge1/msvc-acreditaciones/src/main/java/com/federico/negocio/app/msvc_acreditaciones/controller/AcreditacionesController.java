package com.federico.negocio.app.msvc_acreditaciones.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionRequest;
import com.federico.negocio.app.msvc_acreditaciones.domain.dto.AcreditacionResponse;
import com.federico.negocio.app.msvc_acreditaciones.services.AcreditacionService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequestMapping("/acreditaciones")
@RequiredArgsConstructor
public class AcreditacionesController {

    private final AcreditacionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcreditacionResponse guardarAcreditacion(@RequestBody @Validated AcreditacionRequest acreditacionRequest) {
       return service.guardarAcreditacion(acreditacionRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AcreditacionResponse getAcreditacionById(@PathVariable String id){
        return service.getAcreditacionById(id);
    }
}
