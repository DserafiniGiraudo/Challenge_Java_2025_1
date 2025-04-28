package com.federico.negocio.app.auth_service.services;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.federico.negocio.app.auth_service.domain.Token;
import com.federico.negocio.app.auth_service.domain.User;
import com.federico.negocio.app.auth_service.domain.dto.LoginRequest;
import com.federico.negocio.app.auth_service.domain.dto.RegisterRequest;
import com.federico.negocio.app.auth_service.domain.dto.TokenResponse;
import com.federico.negocio.app.auth_service.exceptions.ConstantsExceptions;
import com.federico.negocio.app.auth_service.exceptions.UserAlreadyExistsException;
import com.federico.negocio.app.auth_service.repository.TokenRepository;
import com.federico.negocio.app.auth_service.repository.UserRepository;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder encoder;
    private final JwtService  jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse register(RegisterRequest  request){
        
         userRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw UserAlreadyExistsException.builder();
                });
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
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
                ));
                
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> NotFoundException.build("User not found"));
        
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
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

    private void revokeAllUserTokens(User user) {
        final List<Token> validUserTokens = tokenRepository.findAllByExpiredIsFalseOrRevokedIsFalseAndUser(user);
        if(!validUserTokens.isEmpty()){
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }


    public TokenResponse refresh(String authheader) {
        if (authheader == null || !authheader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Bearer token");
        }

        final String refreshToken = authheader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);


        if(userEmail == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        final User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->  NotFoundException.build(ConstantsExceptions.USERNAME_NOT_FOUND));
        
        if(!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        final String accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);
    }
}
