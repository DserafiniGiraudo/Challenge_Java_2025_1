package com.federico.negocio.app.auth_service.repository;

import org.springframework.data.repository.CrudRepository;

import com.federico.negocio.app.auth_service.domain.Token;

public interface TokenRepository extends CrudRepository<Token, Long> {
    Token findByToken(String token);
    void deleteByToken(String token);
    boolean existsByToken(String token);    
}
