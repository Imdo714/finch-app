package com.joojoo.global.exception.handleException.auth.apple;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class AppleInvalidTokenResponseException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.APPLE_INVALID_TOKEN_RESPONSE;

    public AppleInvalidTokenResponseException() {
        super(errorCode);
    }

    public AppleInvalidTokenResponseException(Exception exception) {
        super(errorCode, exception);
    }
}
