package com.joojoo.api.tradeLogTag.infrastructure.rdbms;

import com.joojoo.api.tradeLogTag.domain.entity.TradeLogTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeLogTagJpaRepository extends JpaRepository<TradeLogTag, Long> {
}
