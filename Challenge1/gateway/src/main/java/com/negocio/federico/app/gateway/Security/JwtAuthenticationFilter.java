package com.negocio.federico.app.gateway.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtService jwtService;
    
    // Lista de rutas públicas
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/api/v1/auth/*"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        
        // Verifica si la ruta es pública
        if (isPublicPath(path)) {
            log.debug("Acceso a ruta pública: {}", path);
            return chain.filter(exchange);
        }
        
        // Para rutas protegidas, verificar token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        // Si no hay token o no tiene el formato esperado
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Para rutas de Swagger, permitimos el acceso sin token
            if (path.contains("/v3/api-docs") || path.contains("/swagger-ui")) {
                return chain.filter(exchange);
            }
            
            log.warn("Acceso denegado a ruta protegida: {} - Sin token válido", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String token = authHeader.substring(7);
        
        try {
            // Validar el token
            if (!jwtService.validateToken(token)) {
                log.warn("Token inválido para ruta: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            // Extraer información del token
            String username = jwtService.getUsernameFromToken(token);
            // // Crear objeto de autenticación
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            
            // Propagar autenticación al contexto de seguridad y continuar con la cadena de filtros
            // return chain.filter(exchange);
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            
        } catch (Exception e) {
            log.error("Error al procesar token JWT: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    
    private boolean isPublicPath(String path) {
        return PUBLIC_URLS.stream()
                .anyMatch(pattern -> path.matches(pattern.replace("*", ".*")));
    }
}