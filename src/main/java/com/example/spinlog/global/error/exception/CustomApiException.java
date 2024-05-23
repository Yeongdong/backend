package com.example.spinlog.global.error.exception;

import lombok.Getter;

@Getter
public class CustomApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomApiException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
