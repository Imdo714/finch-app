package com.joojoo.api.tradeLog.domain.repository;

import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

public interface TradeLogRepository {
    TradeLog save(TradeLog tradeLog);
}
