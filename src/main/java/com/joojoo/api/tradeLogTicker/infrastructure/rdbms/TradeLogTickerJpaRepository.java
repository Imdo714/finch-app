package com.joojoo.api.tradeLogTicker.infrastructure.rdbms;

import com.joojoo.api.tradeLogTicker.domain.entity.TradeLogTicker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeLogTickerJpaRepository extends JpaRepository<TradeLogTicker, Long> {
}
