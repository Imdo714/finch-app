package com.joojoo.api.ticker.infrastructure.redis;

import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.api.common.search.range.SearchRange;
import com.joojoo.global.exception.handleException.redis.RedisConnectionFailException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TickerRedisRepositoryImpl implements TickerRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String AUTOCOMPLETE_KEY = "autocomplete:stock";

    @Override
    public void addStocksToRedis(Set<String> values) {
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>(); // TypedTuple이 value 와 score를 하나의 객체로 묶어줌
        for (String value : values) {
            tuples.add(ZSetOperations.TypedTuple.of(value, 0.0));
        }
        try {
            redisTemplate.opsForZSet().add(AUTOCOMPLETE_KEY, tuples);
        } catch (Exception e) {
            throw new RedisConnectionFailException();
        }
    }

    @Override
    public Set<String> searchTickerQuery(SearchRange range, int limit) {
        try {
            Range<String> redisRange = Range.rightOpen(range.getStart(), range.getEnd());
            Limit redisLimit = Limit.limit().count(limit);

            return redisTemplate.opsForZSet()
                    .rangeByLex(AUTOCOMPLETE_KEY, redisRange, redisLimit);
        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            log.error("Redis 연결 실패 또는 타임아웃 발생: {}", e.getMessage());
            return Collections.emptySet();
        } catch (Exception e) {
            log.error("Redis 알 수 없는 에러: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

}
