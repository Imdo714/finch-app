package com.joojoo.api.ticker.domain.repository;

import com.joojoo.global.common.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TickerRepository {
    void saveAll(List<Ticker> tickerList);

    List<Ticker> findAll();

    Optional<Ticker> findById(Long tickerId);

    RelatedBlockDetailCountResponse getTickerDetailCount(Long userId, Long tickerId);

    /** Ticker 프록시 객체 반환 */
    Ticker getReferenceById(Long tickerId);
}
