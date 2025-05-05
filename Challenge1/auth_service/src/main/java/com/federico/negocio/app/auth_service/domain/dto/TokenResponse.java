package com.federico.negocio.app.auth_service.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("access_token")
        String jwtToken,
        @JsonProperty("refresh_token")
        String refreshToken) {}
