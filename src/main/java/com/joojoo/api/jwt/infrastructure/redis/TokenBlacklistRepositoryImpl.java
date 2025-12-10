package com.joojoo.api.jwt.infrastructure.redis;

import com.joojoo.api.jwt.domain.repository.TokenBlacklistRepository;
import com.joojoo.global.exception.handleException.redis.RedisConnectionFailException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TokenBlacklistRepositoryImpl implements TokenBlacklistRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void add(String accessToken, Long expiration) {
        if (expiration <= 0) return;

        try {
            redisTemplate.opsForValue()
                    .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            log.error("Redis 연결 실패 또는 타임아웃 발생: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Redis 알 수 없는 에러: {}", e.getMessage());
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(token));
        } catch (Exception e) {
            log.warn("Redis 연결 실패 {}", e.getMessage());
            throw new RedisConnectionFailException();
        }
    }

}
