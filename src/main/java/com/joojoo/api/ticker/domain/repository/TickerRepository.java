package com.joojoo.api.ticker.domain.repository;

import com.joojoo.api.ticker.domain.model.entity.Ticker;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TickerRepository {
    void saveAll(List<Ticker> tickerList);

    List<Ticker> findAll();

    List<Ticker> findAllByTickerNames(Set<String> tickerSymbols);

    Optional<Ticker> findById(Long tickerId);
}
