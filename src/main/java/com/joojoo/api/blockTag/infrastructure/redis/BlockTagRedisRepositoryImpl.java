package com.joojoo.api.blockTag.infrastructure.redis;

import com.joojoo.api.common.search.range.SearchRange;
import com.joojoo.global.exception.handleException.redis.RedisConnectionFailException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BlockTagRedisRepositoryImpl implements BlockTagRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String AUTOCOMPLETE_LEX_KEY = "autocomplete:tags:lex";
    private static final String AUTOCOMPLETE_SCORE_KEY = "autocomplete:tags:score";

    /** 태그 추가 (Score +1) */
    @Override
    public void addTagsToRedis(Long userId, Set<String> lexEntries, Set<Long> tagIds) {
        try {
            // 1. 검색용 키 저장
            for (String entry : lexEntries) {
                redisTemplate.opsForZSet().add(AUTOCOMPLETE_LEX_KEY, entry, 0);
            }

            // 2. 점수용 키 점수 관리
            for (Long tagId : tagIds) {
                String scoreKey = userId + ":" + tagId;
                redisTemplate.opsForZSet().incrementScore(AUTOCOMPLETE_SCORE_KEY, scoreKey, 1.0);
            }
        } catch (Exception e) {
            throw new RedisConnectionFailException();
        }
    }

    /** 태그 삭제 (Score -1, 0이 되면 삭제) */
    @Override
    public void removeTagsFromRedis(Long userId, Long tagId, Set<String> lexEntries, int countToRemove) {
        try {
            String member = userId + ":" + tagId;

            // 점수를 차감 (음수를 더함)
            Double remainingScore = redisTemplate.opsForZSet()
                    .incrementScore(AUTOCOMPLETE_SCORE_KEY, member, (double) -countToRemove);

            // 점수가 없거나 0 이하면 관련 데이터 모두 삭제
            if (remainingScore == null || remainingScore <= 0) {
                // 검색용 LEX 데이터 삭제
                for (String entry : lexEntries) {
                    redisTemplate.opsForZSet().remove(AUTOCOMPLETE_LEX_KEY, entry);
                }
                // 점수 용 SCORE 멤버:태그 삭제
                redisTemplate.opsForZSet().remove(AUTOCOMPLETE_SCORE_KEY, member);
            }
        } catch (Exception e) {
            log.error("Redis 삭제 실패 - 태그ID: {}, 에러: {}", tagId, e.getMessage());
            throw new RedisConnectionFailException();
        }
    }

    @Override
    public Set<String> searchTagQuery(SearchRange range) {
        try {
            Range<String> redisRange = Range.from(Range.Bound.inclusive(range.getStart()))
                    .to(Range.Bound.inclusive(range.getEnd()));

            return redisTemplate.opsForZSet()
                    .rangeByLex(AUTOCOMPLETE_LEX_KEY, redisRange, Limit.limit().count(10));

        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            log.error("Redis 연결 실패 또는 타임아웃 발생: {}", e.getMessage());
            return Collections.emptySet();
        } catch (Exception e) {
            log.error("Redis 알 수 없는 에러: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

}
