package com.federico.negocio.app.auth_service.domain.dto;

public record LoginRequest(
    String email,
    String password) {}