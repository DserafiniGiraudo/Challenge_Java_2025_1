package com.federico.negocio.app.auth_service.domain.dto;

public record RegisterRequest(
    String email,
    String password,
    String name) {} 