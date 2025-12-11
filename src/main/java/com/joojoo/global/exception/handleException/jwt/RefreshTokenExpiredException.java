package com.joojoo.global.exception.handleException.jwt;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class RefreshTokenExpiredException extends ServiceException {

    private static final ErrorCode errorCode = ErrorCode.EXPIRED_REFRESH_TOKEN;

    public RefreshTokenExpiredException() {
        super(errorCode);
    }

    public RefreshTokenExpiredException(Exception exception) {
        super(errorCode, exception);
    }
}
