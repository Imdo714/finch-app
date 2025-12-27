package com.joojoo.api.tradeLog.application;

import com.joojoo.api.tradeLog.presentation.dto.request.TradeRequestDto;

public interface TradeLogService {
    void createTradesLog(Long userId, TradeRequestDto tradeRequestDto);
}
