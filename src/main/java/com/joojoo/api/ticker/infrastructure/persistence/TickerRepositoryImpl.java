package com.joojoo.api.ticker.infrastructure.persistence;

import com.joojoo.api.ticker.infrastructure.rdbms.TickerJpaRepository;
import com.joojoo.global.common.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.ticker.infrastructure.queryDsl.TickerQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TickerRepositoryImpl implements TickerRepository {

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
    public List<Ticker> findAllByTickerNames(Set<String> tickerSymbols) {
        return tickerQueryDslRepository.findAllByTickerNames(tickerSymbols);
    }

    @Override
    public Optional<Ticker> findById(Long tickerId) {
        return tickerJpaRepository.findById(tickerId);
    }

    @Override
    public RelatedBlockDetailCountResponse getTickerDetailCount(Long userId, Long tickerId) {
        return tickerQueryDslRepository.getTickerDetailCount(userId, tickerId);
    }
}
