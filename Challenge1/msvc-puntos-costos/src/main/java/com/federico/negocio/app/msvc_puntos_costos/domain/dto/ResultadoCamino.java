package com.federico.negocio.app.msvc_puntos_costos.domain.dto;

import java.util.List;

public record ResultadoCamino(int costoTotal, List<String> camino) {
}
