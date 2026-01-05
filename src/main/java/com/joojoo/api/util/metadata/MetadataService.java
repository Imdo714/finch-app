package com.joojoo.api.util.metadata;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

public interface MetadataService {

    /** TradeLog 타입으로 티커, 태그 찾기 */
    void processTradeLogMetadata(TradeLog tradeLog, Long userId, Ticker ticker);
}
