package com.federico.negocio.app.msvc_puntos_ventas.domain.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

public record PuntoVentaRequest (
    @NotNull(message = "El campo puntoVenta no puede ser null")
    @NotBlank(message = "El campo puntoVenta no puede estar vacío")
    String puntoVenta
) {}


// @Data
// @AllArgsConstructor
// public class PuntoVentaRequest {

//     @NotBlank(message = "El campo puntoVenta no puede estar vacío")
//     @NotNull(message = "El campo puntoVenta no puede ser null")
//     private String puntoVenta;
// }