package com.joojoo.api.ticker.infrastructure.rdbms;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerJpaRepository extends JpaRepository<Ticker, Long> {
}
