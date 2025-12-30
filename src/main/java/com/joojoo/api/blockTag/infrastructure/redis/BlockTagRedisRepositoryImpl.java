package com.joojoo.api.blockTag.infrastructure.redis;

import com.joojoo.global.exception.handleException.redis.RedisConnectionFailException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BlockTagRedisRepositoryImpl implements BlockTagRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String AUTOCOMPLETE_KEY = "autocomplete:tags";

    /** 태그 추가 (Score +1) */
    @Override
    public void addTagsToRedis(Set<String> values) {
        try {
            for (String value : values) {
                // 기존 0.0으로 덮어쓰는 add 대신 incrementScore 사용
                redisTemplate.opsForZSet().incrementScore(AUTOCOMPLETE_KEY, value, 1.0);
            }
        } catch (Exception e) {
            throw new RedisConnectionFailException();
        }
    }

    /** 태그 삭제 (Score -1, 0이 되면 삭제) */
    @Override
    public void removeTagsFromRedis(Set<String> values) {
        try {
            for (String value : values) {
                Double score = redisTemplate.opsForZSet().incrementScore(AUTOCOMPLETE_KEY, value, -1.0);

                // 점수가 0 이하라면 검색 리스트에서 제거
                if (score != null && score <= 0) {
                    redisTemplate.opsForZSet().remove(AUTOCOMPLETE_KEY, value);
                }
            }
        } catch (Exception e) {
            throw new RedisConnectionFailException();
        }
    }
}
