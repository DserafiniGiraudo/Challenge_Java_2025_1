package com.federico.negocio.app.msvc_puntos_costos.domain.dto;

import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CaminoRequest(
    @NotNull(message = "El punto A y B no pueden ser nulos") 
    CaminoPK caminoPK,
    @NotNull(message = "El costo no puede ser nulo") 
    @Positive(message = "El costo no puede ser negativo")
    int costo
) {}
