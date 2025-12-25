package com.joojoo.global.exception.handleException.block;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class BlockOwnerMismatchException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.BLOCK_FORBIDDEN_ACCESS;

    public BlockOwnerMismatchException() {
        super(errorCode);
    }

    public BlockOwnerMismatchException(Exception exception) {
        super(errorCode, exception);
    }
}
