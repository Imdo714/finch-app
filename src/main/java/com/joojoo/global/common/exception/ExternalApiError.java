package com.joojoo.global.common.exception;

public class ExternalApiError extends RuntimeException {
    public ExternalApiError(String message) {
        super(message);
    }
}
