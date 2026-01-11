package com.joojoo.api.blockTicker.infrastructure.redis;

import java.util.Set;

public interface BlockTickerRedisRepository {
    /** Redis에 사용자가 사용한 티커 저장 */
    void addTickersToRedis(Long userId, Set<String> lexEntries, Set<Long> tickerIds);
}
