package com.joojoo.api.blockTicker.infrastructure.redis;

import com.joojoo.global.exception.handleException.redis.RedisConnectionFailException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BlockTickerRedisRepositoryImpl implements BlockTickerRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String AUTOCOMPLETE_TICKER_LEX_KEY = "autocomplete:tickers:lex";
    private static final String AUTOCOMPLETE_TICKER_SCORE_KEY = "autocomplete:tickers:score";

    @Override
    public void addTickersToRedis(Long userId, Set<String> lexEntries, Set<Long> tickerIds) {
        try {
            // 1. 검색용 키 저장
            for (String entry : lexEntries) {
                redisTemplate.opsForZSet().add(AUTOCOMPLETE_TICKER_LEX_KEY, entry, 0);
            }

            // 2. 점수용 키 점수 관리
            for (Long tickerId : tickerIds) {
                String scoreKey = userId + ":" + tickerId;
                redisTemplate.opsForZSet().incrementScore(AUTOCOMPLETE_TICKER_SCORE_KEY, scoreKey, 1.0);
            }
        } catch (Exception e) {
            throw new RedisConnectionFailException();
        }
    }

}
