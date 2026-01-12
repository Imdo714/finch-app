package com.joojoo.api.tradeLog.application.port.in;

public interface DeleteTradeLogUseCase {
    void deleteTradeLog(Long userId, Long tradeLogId);
}
