package com.negocio.federico.app.gateway.Security;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.negocio.federico.app.gateway.config.SecurityConfig;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        
        // Verifica si la ruta es pública o Swagger
        if (isPublicPath(path) || isSwaggerPath(path)) {
            return chain.filter(exchange);
        }

        // Verifica si la ruta es autenticada
        if(isAuthenticatedPath(path)) {
            // Para rutas protegidas, verificar token
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            // Si no hay token o no tiene el formato esperado
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            String token = authHeader.substring(7);

            // Validar el token
            if (!jwtService.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            // Extraer información del token
            String username = jwtService.getUsernameFromToken(token);
            Collection<? extends GrantedAuthority> authorities = jwtService.getAuthoritiesFromToken(token);

            // Crear objeto de autenticación con toda la información necesaria
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

            try {
                // Propagar autenticación al contexto de seguridad y continuar con la cadena de filtros
                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            // Si la ruta no es pública, Swagger o autenticada, denegar acceso
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
    }
    
    private boolean isPublicPath(String path) {
        return Arrays.stream(SecurityConfig.PUBLIC_URLS)
            .anyMatch(pattern -> pathMatches(path, pattern));
    }
    
    private boolean isSwaggerPath(String path) {
        return Arrays.stream(SecurityConfig.SWAGGER_URLS)
        .anyMatch(pattern -> pathMatches(path, pattern));
    }

    private boolean isAuthenticatedPath(String path) {
        return Arrays.stream(SecurityConfig.AUTHENTICATED_URLS)
            .anyMatch(pattern -> pathMatches(path, pattern));
    }
    
    private boolean pathMatches(String path, String pattern) {
        // Convierte el patrón a una expresión regular
        if (pattern.endsWith("/*")) {
            String basePath = pattern.substring(0, pattern.length() - 1);
            return path.startsWith(basePath);
        } else if (pattern.endsWith("/**")) {
            String basePath = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(basePath);
        } else {
            return path.equals(pattern);
        }
    }
}