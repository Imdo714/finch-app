package com.joojoo.api.tradeLog.application.port.in;

import com.joojoo.api.tradeLog.presentation.dto.request.TradeRequestDto;

public interface CreateTradeLogUseCase {
    void createTradesLog(Long userId, TradeRequestDto tradeRequestDto);
}
