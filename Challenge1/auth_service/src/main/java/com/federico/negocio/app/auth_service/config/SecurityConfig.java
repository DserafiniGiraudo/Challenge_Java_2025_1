package com.federico.negocio.app.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;
import com.federico.negocio.app.auth_service.domain.Token;
import com.federico.negocio.app.auth_service.repository.TokenRepository;


import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;
    private final TokenRepository tokenRepository;

    private static final String[] PUBLIC_URLS = {
        "/auth/register",
        "/auth/login",
        "/auth/refresh"
    };


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, PUBLIC_URLS).permitAll()
                .anyRequest().authenticated())
            .httpBasic(c  -> c.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider)
            // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout->
                logout.logoutUrl("/auth/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                    logout(authHeader);
                })
                .logoutSuccessHandler((request,response,authentication) -> {
                    SecurityContextHolder.clearContext();
                })
            )
            .build();
    } 
    
    private void logout(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }

        final String jwtToken = token.substring(7);
        final Token foundToken = tokenRepository.findByToken(jwtToken)
            .orElseThrow(() -> NotFoundException.build("Token not found"));
        
        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    }
}
