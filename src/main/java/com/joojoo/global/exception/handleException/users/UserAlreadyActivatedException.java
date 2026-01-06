package com.joojoo.global.exception.handleException.users;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class UserAlreadyActivatedException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.USER_ALREADY_ACTIVATED;

    public UserAlreadyActivatedException() {
        super(errorCode);
    }

    public UserAlreadyActivatedException(Exception exception) {
        super(errorCode, exception);
    }
}
