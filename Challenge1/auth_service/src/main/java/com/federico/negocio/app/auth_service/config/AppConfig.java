package com.federico.negocio.app.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.federico.negocio.app.auth_service.domain.Token;
import com.federico.negocio.app.auth_service.domain.User;
import com.federico.negocio.app.auth_service.repository.TokenRepository;
import com.federico.negocio.app.auth_service.repository.UserRepository;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AppConfig {
    
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;
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
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    UserDetailsService userDetailsService() {
        return email -> {
            final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> NotFoundException.build("User not found"));
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
            };
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
       DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       authProvider.setUserDetailsService(userDetailsService());
       authProvider.setPasswordEncoder(passwordEncoder());
       return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
       return config.getAuthenticationManager();
    }

   @Bean
   PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
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
