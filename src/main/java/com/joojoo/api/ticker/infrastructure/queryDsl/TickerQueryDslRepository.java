package com.joojoo.api.ticker.infrastructure.queryDsl;

import com.joojoo.api.ticker.domain.model.entity.Ticker;

import java.util.List;
import java.util.Set;

public interface TickerQueryDslRepository {
    List<Ticker> findAllByTickerNames(Set<String> tickerSymbols);
}
