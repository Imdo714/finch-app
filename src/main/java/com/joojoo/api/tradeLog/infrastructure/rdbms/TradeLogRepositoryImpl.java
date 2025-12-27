package com.joojoo.api.tradeLog.infrastructure.rdbms;

import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TradeLogRepositoryImpl implements TradeLogRepository {

    private final TradeLogJpaRepository tradeLogJpaRepository;

    @Override
    public TradeLog save(TradeLog tradeLog) {
        return tradeLogJpaRepository.save(tradeLog);
    }
}
