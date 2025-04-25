package com.federico.negocio.app.auth_service.services;

import com.federico.negocio.app.auth_service.domain.User;

public interface JwtService {

    String generateToken(User user);
    String generateRefreshToken(User user);
}
