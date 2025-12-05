package com.joojoo.api.jwt.infrastructure.redis;

import com.joojoo.api.jwt.domain.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenBlacklistRepositoryImpl implements TokenBlacklistRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void add(String accessToken, Long expiration) {
        if (expiration > 0) {
            redisTemplate.opsForValue()
                    .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

}
