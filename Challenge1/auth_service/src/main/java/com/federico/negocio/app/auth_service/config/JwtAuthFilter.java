package com.federico.negocio.app.auth_service.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.federico.negocio.app.auth_service.domain.Token;
import com.federico.negocio.app.auth_service.domain.User;
import com.federico.negocio.app.auth_service.repository.TokenRepository;
import com.federico.negocio.app.auth_service.repository.UserRepository;
import com.federico.negocio.app.auth_service.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        String path =  request.getServletPath();

        if (isPublicPath(path) || isSwaggerPath(path)) {
            filter.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filter.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwtToken);

        final Token token = tokenRepository.findByToken(jwtToken).orElse(null);
        if(token == null || token.isExpired() || token.isRevoked()){
            filter.doFilter(request, response);
            return;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        final Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

        if(user.isEmpty()){
            filter.doFilter(request, response);
            return;
        }

        final boolean isTokenValid = jwtService.isTokenValid(jwtToken, user.get());
        if(!isTokenValid){
            return;
        }

        final var authToken = new UsernamePasswordAuthenticationToken(
            user.get(),
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filter.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return Arrays.stream(SecurityConfig.PUBLIC_URLS)
            .anyMatch(pattern -> pathMatches(path, pattern));
    }
    
    private boolean isSwaggerPath(String path) {
        return Arrays.stream(SecurityConfig.SWAGGER_URLS)
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
