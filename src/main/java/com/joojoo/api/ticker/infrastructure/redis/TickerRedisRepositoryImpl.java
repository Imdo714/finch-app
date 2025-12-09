package com.joojoo.api.ticker.infrastructure.redis;

import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

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

        redisTemplate.opsForZSet().add(AUTOCOMPLETE_KEY, tuples);
    }

    @Override
    public Set<String> searchTickerQuery(Range<String> range, Limit limit) {
        return redisTemplate.opsForZSet()
                .rangeByLex(AUTOCOMPLETE_KEY, range, limit);
    }
}
