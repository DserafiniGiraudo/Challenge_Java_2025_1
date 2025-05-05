package com.federico.negocio.app.msvc_acreditaciones.services.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PuntoVentaClientImpl implements PuntoVentaClient {

    private final WebClient.Builder builder;

    @Override
    @CircuitBreaker(name = "acreditacionesCB", fallbackMethod = "fallbackfindById")
    public String findNameById(int id) {
        try {
            return builder.build().get()
                    .uri("/puntosVentas/nombres/{id}", id)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            throw e;
        }
    }

    public String fallbackfindById(int id, Throwable e) {
        return null;
    }
}