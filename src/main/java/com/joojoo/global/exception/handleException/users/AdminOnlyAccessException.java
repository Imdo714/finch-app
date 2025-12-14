package com.joojoo.global.exception.handleException.users;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class AdminOnlyAccessException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.USER_MISMATCH;

    public AdminOnlyAccessException() {
        super(errorCode);
    }

    public AdminOnlyAccessException(Exception exception) {
        super(errorCode, exception);
    }
}
