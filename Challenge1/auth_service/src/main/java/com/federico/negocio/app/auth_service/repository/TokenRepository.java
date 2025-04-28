package com.federico.negocio.app.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.federico.negocio.app.auth_service.domain.Token;
import com.federico.negocio.app.auth_service.domain.User;

public interface TokenRepository extends CrudRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    void deleteByToken(String token);
    boolean existsByToken(String token);
    List<Token> findAllByExpiredIsFalseOrRevokedIsFalseAndUser(User user); 
}
