package com.joojoo.global.exception.handleException.jwt;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class TokenVerificationException extends ServiceException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

    public TokenVerificationException() {
        super(errorCode);
    }

    public TokenVerificationException(Exception exception) {
        super(errorCode, exception);
    }
}
