package com.joojoo.global.exception.handleException.auth;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class InvalidAuthorizationException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.INVALID_AUTHORIZATION_CODE;

    public InvalidAuthorizationException() {
        super(errorCode);
    }

    public InvalidAuthorizationException(Exception exception) {
        super(errorCode, exception);
    }
}
