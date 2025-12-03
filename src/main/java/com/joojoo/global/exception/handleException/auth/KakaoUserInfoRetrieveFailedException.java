package com.joojoo.global.exception.handleException.auth;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class KakaoUserInfoRetrieveFailedException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.KAKAO_USER_INFO_RETRIEVE_FAILED;

    public KakaoUserInfoRetrieveFailedException() {
        super(errorCode);
    }

    public KakaoUserInfoRetrieveFailedException(Exception exception) {
        super(errorCode, exception);
    }
}