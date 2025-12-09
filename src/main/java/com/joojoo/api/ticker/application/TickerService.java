package com.joojoo.api.ticker.application;

import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;

public interface TickerService {
    void addStockToRedis(String name, String ticker);

    TickerSearchResponse search(String query);
}
