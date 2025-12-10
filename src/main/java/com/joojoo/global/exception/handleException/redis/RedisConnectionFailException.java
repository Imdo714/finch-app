package com.joojoo.global.exception.handleException.redis;

import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;

public class RedisConnectionFailException extends ServiceException {
    private static final ErrorCode errorCode = ErrorCode.REDIS_CONNECTION_FAIL;

    public RedisConnectionFailException() {
        super(errorCode);
    }

    public RedisConnectionFailException(Exception exception) {
        super(errorCode, exception);
    }
}
