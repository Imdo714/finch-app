package com.joojoo.api.tradeLog.infrastructure.rdbms;

import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.tradeLog.infrastructure.queryDsl.tradeLogQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeLogRepositoryImpl implements TradeLogRepository {

    private final TradeLogJpaRepository tradeLogJpaRepository;
    private final tradeLogQueryDslRepository tradeLogQueryDslRepository;

    @Override
    public TradeLog save(TradeLog tradeLog) {
        return tradeLogJpaRepository.save(tradeLog);
    }

    @Override
    public List<TradeLog> findAllByIdIn(List<Long> blockIds) {
        return tradeLogQueryDslRepository.findAllByIdIn(blockIds);
    }

    @Override
    public void withdrawByUserId(Long userId) {
        tradeLogQueryDslRepository.withdrawByUserId(userId);
    }
}
