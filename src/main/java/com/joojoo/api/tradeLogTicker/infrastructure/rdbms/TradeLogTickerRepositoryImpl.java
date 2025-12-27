package com.joojoo.api.tradeLogTicker.infrastructure.rdbms;

import com.joojoo.api.tradeLogTicker.domain.entity.TradeLogTicker;
import com.joojoo.api.tradeLogTicker.domain.repository.TradeLogTickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeLogTickerRepositoryImpl implements TradeLogTickerRepository {

    private final TradeLogTickerJpaRepository tickerJpaRepository;

    @Override
    public void saveAll(List<TradeLogTicker> tradeLogTickers) {
        tickerJpaRepository.saveAll(tradeLogTickers);
    }
}
