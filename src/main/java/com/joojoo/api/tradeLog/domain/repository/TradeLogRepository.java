package com.joojoo.api.tradeLog.domain.repository;

import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

import java.util.List;

public interface TradeLogRepository {
    TradeLog save(TradeLog tradeLog);

    List<TradeLog> findAllByIdIn(List<Long> blockIds);

    void withdrawByUserId(Long userId);
}
