package com.joojoo.global.exception.handleException;

import com.joojoo.global.common.exception.ExternalApiError;
import com.joojoo.global.common.response.ApiResponse;
import com.joojoo.global.exception.handleException.auth.SocialAuthException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SocialAuthException.class)
    public ApiResponse<Object> socialAuthException(SocialAuthException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(ExternalApiError.class)
    public ApiResponse<Object> externalApiError(ExternalApiError e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_GATEWAY.value());
        return ApiResponse.of(HttpStatus.BAD_GATEWAY, e.getMessage(), null);
    }

}
