package com.joojoo.api.tradeLog.infrastructure.queryDsl;

import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

import java.util.List;

public interface tradeLogQueryDslRepository {
    List<TradeLog> findAllByIdIn(List<Long> blockIds);

    void withdrawByUserId(Long userId);
}
