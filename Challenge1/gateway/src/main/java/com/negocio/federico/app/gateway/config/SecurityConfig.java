package com.negocio.federico.app.gateway.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.negocio.federico.app.gateway.Security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public static final String[] PUBLIC_URLS = {
        "/api/v1/auth/*"
    };
    
    public static final String[] AUTHENTICATED_URLS = {
        "/api/v1/puntosVentas",  
        "/api/v1/puntosVentas/*",  
        "/api/v1/costos",        
        "/api/v1/costos/*",        
        "/api/v1/acreditaciones",
        "/api/v1/acreditaciones/*" 
    };
    
    public static final String[] SWAGGER_URLS = {
        "/msvc-puntos-ventas/v3/api-docs",
        "/msvc-puntos-costos/v3/api-docs",
        "/msvc-acreditaciones/v3/api-docs",
        "/v3/api-docs/**",  
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
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
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
            .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}