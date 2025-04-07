package com.federico.negocio.app.msvc_puntos_costos.services.client;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PuntoVentaClientImpl implements PuntoVentaClient {
    
    private final WebClient.Builder builder;


    @Override
    public List<PuntoVenta> findAll() {
      
        return builder.build().get()
                .uri("/puntosVentas")
                .retrieve()
                .bodyToFlux(PuntoVenta.class)
                .collectList()
                .block();
    }
    @Override
    public PuntoVenta findById(Integer id) {
       
        return builder.build().get()
                .uri("/puntosVentas/{id}", id)
                .retrieve()
                .bodyToMono(PuntoVenta.class)
                .block();
    }
}