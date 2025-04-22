package com.federico.negocio.app.msvc_acreditaciones.domain.dto;

import java.time.LocalDate;

public record AcreditacionResponse(
    String id,
    int identificadorPuntoVenta,
    String nombrePuntoventa,
    double importe,
    LocalDate fechaPedido) {}