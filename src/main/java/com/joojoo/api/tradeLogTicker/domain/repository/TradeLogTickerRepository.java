package com.joojoo.api.tradeLogTicker.domain.repository;

import com.joojoo.api.tradeLogTicker.domain.entity.TradeLogTicker;

import java.util.List;

public interface TradeLogTickerRepository {
    void saveAll(List<TradeLogTicker> tradeLogTickers);
}
