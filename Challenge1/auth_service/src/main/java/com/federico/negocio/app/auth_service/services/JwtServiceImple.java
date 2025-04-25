package com.federico.negocio.app.auth_service.services;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.federico.negocio.app.auth_service.domain.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImple implements JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public String generateToken(final User user) {
       return buildToken(user, jwtExpiration);
    }

    public String generateRefreshToken(final User user) {
       return buildToken(user, refreshTokenExpiration);
    }

    private String buildToken(final User user, final long expiration) {
       
       return Jwts.builder()
                .id(Long.toString(expiration))
                .claims(Map.of("name",user.getName()))
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()  + expiration))
                .signWith(getSignInKey())
                .compact();
                
    }

    private SecretKey getSignInKey() {
       byte [] keyBytes = Decoders.BASE64.decode(secretKey);
       return Keys.hmacShaKeyFor(keyBytes);
    }
}