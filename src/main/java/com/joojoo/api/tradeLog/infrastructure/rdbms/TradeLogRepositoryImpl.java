package com.joojoo.api.tradeLog.infrastructure.rdbms;

import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.tradeLog.infrastructure.queryDsl.tradeLogQueryDslRepository;
import com.joojoo.global.exception.handleException.tradeLog.TradeLogNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TradeLogRepositoryImpl implements TradeLogRepository {

    private final TradeLogJpaRepository tradeLogJpaRepository;
    private final tradeLogQueryDslRepository tradeLogQueryDslRepository;
    private final BlockTagRepository blockTagRepository;
    private final BlockTickerRepository blockTickerRepository;

    @Override
    public TradeLog save(TradeLog tradeLog) {
        return tradeLogJpaRepository.save(tradeLog);
    }

    @Override
    public void withdrawByUserId(Long userId) {
        tradeLogQueryDslRepository.withdrawByUserId(userId);
    }

    @Override
    public TradeLog getTradeLogById(Long traderLogId) {
        return tradeLogJpaRepository.findById(traderLogId)
                .orElseThrow(TradeLogNotFoundException::new);
    }

    @Override
    public void clearMetadataByTradeLog(Long tradeLogId) {
        blockTagRepository.deleteByTradeLogId(tradeLogId);
        blockTickerRepository.deleteByTradeLogId(tradeLogId);
        tradeLogQueryDslRepository.deleteByTradeLogId(tradeLogId);
    }

}
