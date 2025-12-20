package com.joojoo.global.exception.handleException.block;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class BlockNotFoundException extends ServiceException  {
    private static final ErrorCode errorCode = ErrorCode.BLOCK_NOT_FOUND;

    public BlockNotFoundException() {
        super(errorCode);
    }

    public BlockNotFoundException(Exception exception) {
        super(errorCode, exception);
    }
}
