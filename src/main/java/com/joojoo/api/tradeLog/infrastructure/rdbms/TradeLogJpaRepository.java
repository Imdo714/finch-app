package com.joojoo.api.tradeLog.infrastructure.rdbms;

import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeLogJpaRepository extends JpaRepository<TradeLog, Long> {

}
