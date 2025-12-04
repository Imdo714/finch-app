package com.joojoo.global.exception;

import com.joojoo.global.exception.enums.ErrorCode;
import lombok.Getter;

/**
 * 비즈니스 로직에서 사용하는 커스텀 런타임 예외
 */
@Getter
public class ServiceException extends RuntimeException {
    private final ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}