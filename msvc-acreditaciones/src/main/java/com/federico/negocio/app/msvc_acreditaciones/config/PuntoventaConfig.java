package com.federico.negocio.app.msvc_acreditaciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PuntoventaConfig {

    @Value("${services.msvc-puntos-ventas.url}")
    private String urlPuntosVentas;

    @Bean
    @LoadBalanced
    WebClient.Builder builder() {
        return WebClient.builder().baseUrl(urlPuntosVentas);
    }
}
