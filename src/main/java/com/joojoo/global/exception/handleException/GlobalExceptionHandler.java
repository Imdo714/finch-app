package com.joojoo.global.exception.handleException;

import com.joojoo.global.common.response.ErrorResponse;
import com.joojoo.global.exception.ServiceException;
import com.joojoo.global.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ServiceException 처리
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException serviceException) {
        ErrorCode errorCode = serviceException.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode, serviceException.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getHttpStatus()));
    }

    // 일반 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}