package com.federico.negocio.app.auth_service.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.federico.negocio.app.auth_service.domain.dto.LoginRequest;
import com.federico.negocio.app.auth_service.domain.dto.RegisterRequest;
import com.federico.negocio.app.auth_service.domain.dto.TokenResponse;
import com.federico.negocio.app.auth_service.services.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse postMethodName(@RequestBody final RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@RequestBody final LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
    

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authheader) {
        return authService.refresh(authheader);
    }
}
