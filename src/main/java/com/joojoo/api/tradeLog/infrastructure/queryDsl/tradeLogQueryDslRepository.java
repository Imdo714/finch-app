package com.joojoo.api.tradeLog.infrastructure.queryDsl;

import com.joojoo.api.tradeLog.domain.service.calculate.TradeMetrics;
import com.joojoo.api.tradeLog.presentation.dto.response.chartOverlay.ChartOverlayResponse;

import java.util.List;

public interface tradeLogQueryDslRepository {
    void withdrawByUserId(Long userId);

    void deleteByTradeLogId(Long userId, Long tradeLogId);

    TradeMetrics findAllBuyLogsByTicker(Long userId, Long tickerId);

    List<ChartOverlayResponse.TradeDetailDto> getTradeBuySellRecords(Long userId, Long tickerId);
}
