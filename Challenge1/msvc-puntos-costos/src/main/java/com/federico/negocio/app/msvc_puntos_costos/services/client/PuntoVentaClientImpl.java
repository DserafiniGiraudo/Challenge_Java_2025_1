package com.federico.negocio.app.msvc_puntos_costos.services.client;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PuntoVentaClientImpl implements PuntoVentaClient {
    
    private final WebClient.Builder builder;

    @Override
    @CircuitBreaker(name = "costosCB", fallbackMethod = "fallbackFindAll")
    public List<PuntoVenta> findAll() {
        return builder.build().get()
                .uri("/puntosVentas")
                .retrieve()
                .bodyToFlux(PuntoVenta.class)
                .collectList()
                .block();
    }

    @Override
    @CircuitBreaker(name = "costosCB", fallbackMethod = "fallbackfindById")
    public PuntoVenta findById(Integer id) {
        return builder.build().get()
                .uri("/puntosVentas/{id}", id)
                .retrieve()
                .bodyToMono(PuntoVenta.class)
                .block();
    }

    public List<PuntoVenta> fallbackFindAll(Throwable e) {
        return Collections.emptyList();
    }

    public PuntoVenta fallbackfindById(Integer id, Throwable e) {
        return null;
    }
}