package com.joojoo.global.exception.handleException.auth;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class KakaoInvalidUserResponseException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.KAKAO_INVALID_USER_RESPONSE;

    public KakaoInvalidUserResponseException() {
        super(errorCode);
    }

    public KakaoInvalidUserResponseException(Exception exception) {
        super(errorCode, exception);
    }
}