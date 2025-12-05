package com.joojoo.global.exception.handleException.auth.kakao;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

    public class KakaoTokenIssueFailedException extends ServiceException {
        private static final ErrorCode errorCode = ErrorCode.KAKAO_TOKEN_ISSUE_FAILED;

        public KakaoTokenIssueFailedException() {
            super(errorCode);
        }

    public KakaoTokenIssueFailedException(Exception exception) {
        super(errorCode, exception);
    }
    // 생성자 2개
}