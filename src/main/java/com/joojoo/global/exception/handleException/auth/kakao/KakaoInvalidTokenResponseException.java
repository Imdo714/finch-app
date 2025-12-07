package com.joojoo.global.exception.handleException.auth.kakao;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class KakaoInvalidTokenResponseException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.KAKAO_INVALID_TOKEN_RESPONSE;

    public KakaoInvalidTokenResponseException() {
        super(errorCode);
    }

    public KakaoInvalidTokenResponseException(Exception exception) {
        super(errorCode, exception);
    }
}