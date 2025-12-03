package com.joojoo.global.common.response;

import com.joojoo.global.exception.enums.ErrorCode;
import lombok.Builder;

/**
 * 예외 발생 시 클라이언트로 반환되는 에러 응답 객체
 */
@Builder
public record ErrorResponse(
    int httpStatus, // HTTP 상태 코드
    String message, // 사용자에게 보여질 메시지
    String code,    // 에러 식별 코드
    String detailMessage    // 상태 메시지
) {
    public ErrorResponse(ErrorCode errorCode, String detailMessage) {
        this(
            errorCode.getHttpStatus(),
            errorCode.getMessage(),
            errorCode.getCode(),
            detailMessage
        );
    }

    public ErrorResponse(ErrorCode errorCode) {
        this(
            errorCode.getHttpStatus(),
            errorCode.getMessage(),
            errorCode.getCode(),
            null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .httpStatus(errorCode.getHttpStatus())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();
    }
}