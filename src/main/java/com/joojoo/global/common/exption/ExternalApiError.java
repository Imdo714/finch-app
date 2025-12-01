package com.joojoo.global.common.exption;

public class ExternalApiError extends RuntimeException {
    public ExternalApiError(String message) {
        super(message);
    }
}
