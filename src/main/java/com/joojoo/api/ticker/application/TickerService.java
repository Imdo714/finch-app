package com.joojoo.api.ticker.application;

public interface TickerService {
    void addStockToRedis(String name, String ticker);

    // 운영 DB에 있는 주식을 Redis에 저장
    void loadTickersToCache(Long userId);
}
