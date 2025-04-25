package com.federico.negocio.app.auth_service.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.federico.negocio.app.auth_service.domain.Token;
import com.federico.negocio.app.auth_service.domain.User;
import com.federico.negocio.app.auth_service.domain.dto.LoginRequest;
import com.federico.negocio.app.auth_service.domain.dto.RegisterRequest;
import com.federico.negocio.app.auth_service.domain.dto.TokenResponse;
import com.federico.negocio.app.auth_service.repository.TokenRepository;
import com.federico.negocio.app.auth_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder encoder;
    private final JwtService  jwtService;

    public TokenResponse register(RegisterRequest  request){
        
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(encoder.encode(request.password()))
                .build();

        var savedUser = userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken,refreshToken);
    }
    public TokenResponse login(LoginRequest request) {
        return null;
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    public TokenResponse refresh(String authheader) {
        return null;
    }
}
