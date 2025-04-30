package com.negocio.federico.app.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.negocio.federico.app.gateway.Security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public static final String[] PUBLIC_URLS = {
        "/api/v1/auth/*",
    };

    public static final String[] AUTHENTICATED_URLS = {
        "/api/v1/puntosVentas/*",
        "/api/v1/costos/*",
        "/api/v1/acreditaciones/*"  // Corregido de "acrediaciones"
    };
    
    public static final String[] SWAGGER_URLS = {
        "/msvc-puntos-ventas/v3/api-docs",
        "/msvc-puntos-costos/v3/api-docs",
        "/msvc-acreditaciones/v3/api-docs",
        "/v3/api-docs",
        "/swagger-ui.html",
        "/webjars/**",
        "/swagger-ui/**"
    };

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                .pathMatchers(PUBLIC_URLS).permitAll()
                .pathMatchers(SWAGGER_URLS).permitAll()
                .pathMatchers(AUTHENTICATED_URLS).authenticated()
                .anyExchange().denyAll()
            )
            // Usar NoOpServerSecurityContextRepository para que no guarde la sesión
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            // Deshabilitar el login basado en formularios
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            // Deshabilitar la autenticación HTTP Basic
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            // Agregar nuestro filtro JWT antes del filtro de autenticación de Spring Security
            .addFilterBefore(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}