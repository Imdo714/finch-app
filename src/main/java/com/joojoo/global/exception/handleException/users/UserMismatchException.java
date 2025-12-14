package com.joojoo.global.exception.handleException.users;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class UserMismatchException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.USER_MISMATCH;

    public UserMismatchException() {
        super(errorCode);
    }

    public UserMismatchException(Exception exception) {
        super(errorCode, exception);
    }
}
