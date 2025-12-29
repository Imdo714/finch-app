package com.joojoo.global.exception.handleException.filter;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class FilterInputDuplicateException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.DUPLICATE_FILTER_INPUT;

    public FilterInputDuplicateException() {
        super(errorCode);
    }

    public FilterInputDuplicateException(Exception exception) {
        super(errorCode, exception);
    }
}
