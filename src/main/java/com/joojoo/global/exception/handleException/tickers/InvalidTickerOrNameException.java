package com.joojoo.global.exception.handleException.tickers;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class InvalidTickerOrNameException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.INVALID_TICKER_NAME;

    public InvalidTickerOrNameException() {
        super(errorCode);
    }

    public InvalidTickerOrNameException(Exception exception) {
        super(errorCode, exception);
    }
}
