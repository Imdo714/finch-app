package com.joojoo.api.ticker.domain.repository;

import com.joojoo.api.ticker.domain.model.entity.Ticker;

import java.util.List;

public interface TickerRepository {
    void saveAll(List<Ticker> tickerList);

    List<Ticker> findAll();
}
