package com.joojoo.global.exception.handleException.tradeLog;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class NotTradeLogOwnerException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.TRADE_LOG_OWNER_FOUND;

    public NotTradeLogOwnerException() {
        super(errorCode);
    }

    public NotTradeLogOwnerException(Exception exception) {
        super(errorCode, exception);
    }
}
