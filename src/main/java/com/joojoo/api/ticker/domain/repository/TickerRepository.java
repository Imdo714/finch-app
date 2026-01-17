package com.joojoo.api.ticker.domain.repository;

import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.model.enums.MarketType;

import java.util.List;
import java.util.Optional;

public interface TickerRepository {
    void saveAll(List<Ticker> tickerList);

    List<Ticker> findAll();

    Optional<Ticker> findById(Long tickerId);

    RelatedBlockDetailCountResponse getTickerDetailCount(Long userId, Long tickerId);

    /** Ticker 프록시 객체 반환 */
    Ticker getReferenceById(Long tickerId);

    /** Ticker 존재 여부 확인 */
    boolean existsById(Long tickerId);

    /** Market이 type인 티커 조회 */
    List<Ticker> findByTickerMarket(MarketType type);

    /** 국내 시장 Ticker 조회 */
    List<Ticker> findKoreaTickers(List<MarketType> marketTypes);
}
