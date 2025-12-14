package com.joojoo.api.ticker.infrastructure.rdbms;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TickerRepositoryImpl implements TickerRepository {

    private final TickerJpaRepository tickerJpaRepository;


    @Override
    public void saveAll(List<Ticker> tickerList) {
        tickerJpaRepository.saveAll(tickerList);
    }

    @Override
    public List<Ticker> findAll() {
        return tickerJpaRepository.findAll();
    }
}
