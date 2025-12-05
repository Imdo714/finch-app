package com.joojoo.api.jwt.domain.repository;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.user.domain.model.entity.User;

import java.util.Optional;

public interface TokenRepository {
    Optional<Token> findByUser(User user);

    Optional<Token> findByUserId(Long userId);

    Token save(Token token);

    void delete(Long userId);
}
