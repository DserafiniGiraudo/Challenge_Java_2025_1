package com.negocio.federico.app.gateway.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /**
     * Extrae el nombre de usuario del token
     * @param token JWT token
     * @return nombre de usuario
     */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrae los roles del token y los convierte en SimpleGrantedAuthority
     * @param token JWT token
     * @return Colección de authorities
     */
    @SuppressWarnings("unchecked")
    public Collection<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getClaims(token);
        List<String> roles = claims.get("roles", List.class);
        
        if (roles == null) {
            log.warn("No se encontraron roles en el token");
            return Collections.emptyList();
        }
        
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    /**
     * Valida si el token es válido
     * @param token JWT token
     * @return true si es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene las claims del token
     * @param token JWT token
     * @return Claims del token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la clave de firma del token
     * @return SecretKey
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}