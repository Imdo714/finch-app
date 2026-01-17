package com.joojoo.api.ticker.infrastructure.queryDsl;

import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.model.enums.MarketType;

import java.util.List;
import java.util.Set;

public interface TickerQueryDslRepository {
    List<Ticker> findAllByTickerNames(Set<String> tickerSymbols);

    RelatedBlockDetailCountResponse getTickerDetailCount(Long userId, Long tickerId);

    List<Ticker> findByTickerMarket(MarketType type);

    List<Ticker> findKoreaTickers(List<MarketType> marketTypes);
}
