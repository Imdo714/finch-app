package com.joojoo.global.exception.handleException.filter;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class FilterInputInvalidException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.INVALID_FILTER_INPUT;

    public FilterInputInvalidException() {
        super(errorCode);
    }

    public FilterInputInvalidException(Exception exception) {
        super(errorCode, exception);
    }
}
