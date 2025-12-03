package com.joojoo.api.jwt.domain.repository;

import com.joojoo.api.jwt.domain.model.entity.Token;

import java.util.Optional;

public interface TokenRepository {
    Token save(Token refreshToken);
    Optional<Token> findByUserId(Long userId);
}
