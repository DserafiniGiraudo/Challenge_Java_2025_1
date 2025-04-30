package com.negocio.federico.app.gateway.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.negocio.federico.app.gateway.model.Token;
import com.negocio.federico.app.gateway.model.User;

public interface TokenRepository extends CrudRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    void deleteByToken(String token);
    boolean existsByToken(String token);
    List<Token> findAllByExpiredIsFalseOrRevokedIsFalseAndUser(User user); 
}