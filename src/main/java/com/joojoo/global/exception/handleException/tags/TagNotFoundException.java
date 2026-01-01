package com.joojoo.global.exception.handleException.tags;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class TagNotFoundException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.TAG_NOT_FOUND;

    public TagNotFoundException() {
        super(errorCode);
    }

    public TagNotFoundException(Exception exception) {
        super(errorCode, exception);
    }
}
