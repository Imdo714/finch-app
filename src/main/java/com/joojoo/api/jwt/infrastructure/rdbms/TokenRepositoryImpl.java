package com.joojoo.api.jwt.infrastructure.rdbms;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.jwt.domain.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public Token save(Token refreshToken) {
        return tokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<Token> findByUserId(Long userId) {
        return tokenJpaRepository.findByUser_UserId(userId);
    }

}
