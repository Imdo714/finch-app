package com.joojoo.api.ticker.infrastructure.persistence;

import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.ticker.application.port.in.TickerInService;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.model.enums.MarketType;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.ticker.infrastructure.queryDsl.TickerQueryDslRepository;
import com.joojoo.api.ticker.infrastructure.rdbms.TickerJpaRepository;
import com.joojoo.global.exception.handleException.tickers.TickerNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class TickerRepositoryImpl implements
        TickerRepository, TickerInService {

    private final TickerJpaRepository tickerJpaRepository;
    private final TickerQueryDslRepository tickerQueryDslRepository;

    @Override
    public void saveAll(List<Ticker> tickerList) {
        tickerJpaRepository.saveAll(tickerList);
    }

    @Override
    public List<Ticker> findAll() {
        return tickerJpaRepository.findAll();
    }

    @Override
    public Optional<Ticker> findById(Long tickerId) {
        return tickerJpaRepository.findById(tickerId);
    }

    @Override
    public RelatedBlockDetailCountResponse getTickerDetailCount(Long userId, Long tickerId) {
        return tickerQueryDslRepository.getTickerDetailCount(userId, tickerId);
    }

    @Override
    public Ticker getReferenceById(Long tickerId) {
        try {
            return tickerJpaRepository.getReferenceById(tickerId);
        } catch (EntityNotFoundException e) {
            throw new TickerNotFoundException();
        }
    }

    @Override
    public boolean existsById(Long tickerId) {
        return tickerJpaRepository.existsById(tickerId);
    }

    @Override
    public List<Ticker> findByTickerMarket(MarketType type) {
        return tickerQueryDslRepository.findByTickerMarket(type);
    }

    @Override
    public List<Ticker> findKoreaTickers(List<MarketType> marketTypes) {
        return tickerQueryDslRepository.findKoreaTickers(marketTypes);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Ticker> getTickerMap(Set<String> identifiers) {
        if (identifiers.isEmpty()) return Collections.emptyMap();

        List<Ticker> tickers = tickerQueryDslRepository.findAllByTickerNames(identifiers);

        Map<String, Ticker> resultMap = new HashMap<>();
        for (Ticker t : tickers) {
            resultMap.put(t.getName(), t);
            resultMap.put(t.getSymbol(), t);
        }
        return resultMap;
    }

}
