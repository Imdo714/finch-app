package com.joojoo.api.ticker.application;

public interface TickerService {
    void addStockToRedis(String name, String ticker);
}
