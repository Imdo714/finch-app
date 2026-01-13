package com.joojoo.api.tradeLog.infrastructure.queryDsl;

import com.joojoo.api.tradeLog.domain.service.calculate.TradeMetrics;

public interface tradeLogQueryDslRepository {
    void withdrawByUserId(Long userId);

    void deleteByTradeLogId(Long userId, Long tradeLogId);

    TradeMetrics findAllBuyLogsByTicker(Long userId, Long tickerId);
}
