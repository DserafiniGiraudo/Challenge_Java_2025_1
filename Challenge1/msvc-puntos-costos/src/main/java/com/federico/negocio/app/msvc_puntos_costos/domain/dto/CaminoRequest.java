package com.federico.negocio.app.msvc_puntos_costos.domain.dto;

import com.federico.negocio.app.msvc_puntos_costos.domain.CaminoPK;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CaminoRequest {
    
    @NotNull(message = "El punto A y B no pueden ser nulos")
    private CaminoPK caminoPK;
    
    @NotNull(message = "El costo no puede ser nulo")
    @Positive(message = "El costo no puede ser negativo")
    private int costo;
}
