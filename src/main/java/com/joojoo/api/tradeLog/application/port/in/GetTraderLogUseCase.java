package com.joojoo.api.tradeLog.application.port.in;

import com.joojoo.api.tradeLog.presentation.dto.request.calculate.TradeCalculateRequest;
import com.joojoo.api.tradeLog.presentation.dto.response.calculate.TradeMetricsResponse;

public interface GetTraderLogUseCase {
    TradeMetricsResponse getTradeLogCalculate(Long userId, TradeCalculateRequest tradeCalculateRequest);
}
