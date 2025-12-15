package com.joojoo.global.exception.handleException.users;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class UserNameDuplicatedException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.USER_NAME_DUPLICATED;

    public UserNameDuplicatedException() {
        super(errorCode);
    }

    public UserNameDuplicatedException(Exception exception) {
        super(errorCode, exception);
    }
}
