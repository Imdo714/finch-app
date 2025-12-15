package com.joojoo.global.exception.handleException.users;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class UserNameRequiredException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.USER_NAME_REQUIRED;

    public UserNameRequiredException() {
        super(errorCode);
    }

    public UserNameRequiredException(Exception exception) {
        super(errorCode, exception);
    }
}
