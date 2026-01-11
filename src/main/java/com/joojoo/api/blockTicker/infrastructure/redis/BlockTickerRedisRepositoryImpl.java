package com.joojoo.api.blockTicker.infrastructure.redis;

import com.joojoo.global.exception.handleException.redis.RedisConnectionFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Slf4j
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

    @Override
    public void removeTickersFromRedis(Long userId, Long tickerId, Set<String> lexEntries, int countToRemove) {
        try {
            String member = userId + ":" + tickerId;

            // 점수를 차감 (음수를 더함)
            Double remainingScore = redisTemplate.opsForZSet()
                    .incrementScore(AUTOCOMPLETE_TICKER_SCORE_KEY, member, (double) -countToRemove);

            // 점수가 없거나 0 이하면 관련 데이터 모두 삭제
            if (remainingScore == null || remainingScore <= 0) {
                // 검색용 LEX 데이터 삭제
                for (String entry : lexEntries) {
                    redisTemplate.opsForZSet().remove(AUTOCOMPLETE_TICKER_LEX_KEY, entry);
                }
                // 점수 용 SCORE 멤버:태그 삭제
                redisTemplate.opsForZSet().remove(AUTOCOMPLETE_TICKER_SCORE_KEY, member);
            }
        } catch (Exception e) {
            log.error("Redis 삭제 실패 - 태그ID: {}, 에러: {}", tickerId, e.getMessage());
            throw new RedisConnectionFailException();
        }
    }

}
