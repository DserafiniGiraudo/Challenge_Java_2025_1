package com.federico.negocio.app.msvc_acreditaciones.services.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PuntoVentaClientImpl implements PuntoVentaClient {

    private final WebClient.Builder builder;

    @Override
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
}