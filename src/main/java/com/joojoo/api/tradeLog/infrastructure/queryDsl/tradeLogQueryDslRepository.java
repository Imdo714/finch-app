package com.joojoo.api.tradeLog.infrastructure.queryDsl;

public interface tradeLogQueryDslRepository {
    void withdrawByUserId(Long userId);

    void deleteByTradeLogId(Long userId, Long tradeLogId);
}
