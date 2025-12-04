package com.joojoo.global.exception.handleException.auth.apple;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class AppleInvalidIdTokenException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.APPLE_INVALID_ID_TOKEN;

    public AppleInvalidIdTokenException() {
        super(errorCode);
    }

    public AppleInvalidIdTokenException(Exception exception) {
        super(errorCode, exception);
    }
}
