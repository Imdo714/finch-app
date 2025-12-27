package com.joojoo.global.exception.handleException.tickers;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class TickerNotFoundException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.TICKER_NOT_FOUND;

    public TickerNotFoundException() {
        super(errorCode);
    }

    public TickerNotFoundException(Exception exception) {
        super(errorCode, exception);
    }
}
