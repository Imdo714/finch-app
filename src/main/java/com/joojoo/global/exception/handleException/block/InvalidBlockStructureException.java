package com.joojoo.global.exception.handleException.block;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class InvalidBlockStructureException extends ServiceException {

    public InvalidBlockStructureException(ErrorCode errorCode) {
        super(errorCode);
    }
}
