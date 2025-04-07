package com.federico.negocio.app.msvc_puntos_costos.services.client;

import java.time.Duration;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;

@Service
public class PuntoVentaClientImpl implements PuntoVentaClient {
    
    private final WebClient webClient;

    public PuntoVentaClientImpl(WebClient.Builder webClientBuilder) {
        System.out.println("PEGANDOLE AL SERVICIO DE PUNTOS VENTA");
        this.webClient = webClientBuilder.baseUrl("http://msvc-puntos-ventas").build();
    }

    @Override
    public List<PuntoVenta> findAll() {
      
        return webClient.get().uri("/puntosVentas")
                .retrieve()
                .bodyToFlux(PuntoVenta.class)
                .collectList()
                .block(Duration.ofSeconds(5));
    }
    @Override
    public PuntoVenta findById(Integer id) {
       
        return webClient.get().uri("/puntosVentas/{id}", id)
                .retrieve()
                .bodyToMono(PuntoVenta.class)
                .block(Duration.ofSeconds(5));
    }
}