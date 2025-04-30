package com.negocio.federico.app.gateway.Security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;
import com.negocio.federico.app.gateway.model.Token;
import com.negocio.federico.app.gateway.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final AuthenticationProvider authenticationProvider;
        private final JwtFilter jwtFilter;
        private final TokenRepository tokenRepository;

        private static final String[] PUBLIC_URLS = {
                        "/api/v1/auth/*",
        };

        private static final String[] AUTHENTICATED_URLS = {
                        "/api/v1/puntosVentas/*",
                        "/api/v1/costos/*",
                        "/api/v1/acrediaciones/*"
        };
        private static final String[] SWAGGER_URLS = {
                        "/msvc-puntos-ventas/v3/api-docs",
                        "/msvc-puntos-costos/v3/api-docs",
                        "/msvc-acreditaciones/v3/api-docs",
                        "/v3/api-docs",
        };

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(PUBLIC_URLS).permitAll()
                                                .requestMatchers(SWAGGER_URLS).permitAll()
                                                .requestMatchers(AUTHENTICATED_URLS).authenticated()
                                                .anyRequest().denyAll())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                .logout(logout -> logout.logoutUrl("/auth/logout")
                                                .addLogoutHandler((request, response, authentication) -> {
                                                        final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                                                        logout(authHeader);
                                                }).logoutSuccessHandler((request, response, authentication) -> {
                                                        SecurityContextHolder.clearContext();
                                                }))
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