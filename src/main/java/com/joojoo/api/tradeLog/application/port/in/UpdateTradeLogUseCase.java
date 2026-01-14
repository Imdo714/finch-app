package com.joojoo.api.tradeLog.application.port.in;

import com.joojoo.api.tradeLog.presentation.dto.request.update.UpdateTradeRequestDto;

public interface UpdateTradeLogUseCase {
    void updateTradeLog(Long userId, UpdateTradeRequestDto updateTradeRequestDto);
}
