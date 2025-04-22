package com.federico.negocio.app.msvc_acreditaciones;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {
    
    @Bean
    @LoadBalanced
    WebClient.Builder webClient(){
        return WebClient.builder();
    }
}
