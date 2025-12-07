package com.joojoo.global.exception.handleException.users;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class UserNotFoundException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

    public UserNotFoundException() {
        super(errorCode);
    }

    public UserNotFoundException(Exception exception) {
        super(errorCode, exception);
    }
}
