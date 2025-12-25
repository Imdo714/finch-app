package com.joojoo.global.exception.handleException.block;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class BlockPromotionLimitExceededException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.BLOCK_PROMOTION_LIMIT_EXCEEDED;

    public BlockPromotionLimitExceededException() {
        super(errorCode);
    }

    public BlockPromotionLimitExceededException(Exception exception) {
        super(errorCode, exception);
    }
}
