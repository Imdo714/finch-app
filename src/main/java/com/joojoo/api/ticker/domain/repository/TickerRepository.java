package com.joojoo.api.ticker.domain.repository;

import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.ticker.domain.model.entity.Ticker;

import java.util.List;
import java.util.Optional;

public interface TickerRepository {
    void saveAll(List<Ticker> tickerList);

    List<Ticker> findAll();

    Optional<Ticker> findById(Long tickerId);

    RelatedBlockDetailCountResponse getTickerDetailCount(Long userId, Long tickerId);

    /** Ticker 프록시 객체 반환 */
    Ticker getReferenceById(Long tickerId);
}
