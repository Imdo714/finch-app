package com.joojoo.global.exception.handleException.tradeLog;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class TradeLogNotFoundException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.TRADE_LOG_NOT_FOUND;

    public TradeLogNotFoundException() {
        super(errorCode);
    }

    public TradeLogNotFoundException(Exception exception) {
        super(errorCode, exception);
    }
}
