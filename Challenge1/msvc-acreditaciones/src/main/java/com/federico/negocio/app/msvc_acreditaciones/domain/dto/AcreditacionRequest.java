package com.federico.negocio.app.msvc_acreditaciones.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AcreditacionRequest(

        @NotNull(message = "El identificador del punto de venta no puede ser nulo.") 
        @Positive(message = "El identificador del punto de venta debe ser un número positivo.") 
        int identificadorPuntoVenta,

        @NotNull(message = "El importe no puede ser nulo.") 
        @Positive(message = "El importe debe ser un número positivo.") 
        double importe){}