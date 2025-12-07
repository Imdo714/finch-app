package com.joojoo.api.jwt.infrastructure.rdbms;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.jwt.domain.repository.TokenRepository;
import com.joojoo.api.user.domain.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public Optional<Token> findByUser(User user) {
        return tokenJpaRepository.findByUser(user);
    }

    @Override
    public Optional<Token> findByUserId(Long userId) {
        return tokenJpaRepository.findByUserId(userId);
    }

    @Override
    public Token save(Token token) {
        return tokenJpaRepository.save(token);
    }

    @Override
    public void delete(Long userId) {
        tokenJpaRepository.deleteByUserId(userId);
    }
}
