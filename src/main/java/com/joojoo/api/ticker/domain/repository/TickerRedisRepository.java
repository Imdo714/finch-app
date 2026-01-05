package com.joojoo.api.ticker.domain.repository;

import com.joojoo.api.ticker.presentation.dto.request.range.TickerSearchRange;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;

import java.util.Set;

public interface TickerRedisRepository {
    // Redis ZSet 자료구조에 주식 이름 저장 
    void addStocksToRedis(Set<String> values);

    /** Redis ZSet 자료구조에서 관련 검색어 찾기 */
    Set<String> searchTickerQuery(TickerSearchRange range, int limit);

}
