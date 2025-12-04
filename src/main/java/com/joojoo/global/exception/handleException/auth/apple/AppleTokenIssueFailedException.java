package com.joojoo.global.exception.handleException.auth.apple;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class AppleTokenIssueFailedException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.APPLE_TOKEN_ISSUE_FAILED;

    public AppleTokenIssueFailedException() {
        super(errorCode);
    }

    public AppleTokenIssueFailedException(Exception exception) {
        super(errorCode, exception);
    }
}
