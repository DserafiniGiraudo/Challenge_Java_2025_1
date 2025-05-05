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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

import java.util.Arrays;

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

    public static final String[] PUBLIC_URLS = {
        "/auth/register",
        "/auth/login",
        "/auth/refresh"
    };

    public static final String[ ] SWAGGER_URLS = {
        "/v3/api-docs",
        "/swagger-ui/**",
        "/webjars/**",
        "/swagger-resources/**",
        "/auth-service/swagger-ui/**"
    };


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // .requestMatchers(PUBLIC_URLS).permitAll()
                // .requestMatchers(SWAGGER_URLS).permitAll()
                // .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().permitAll())
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
