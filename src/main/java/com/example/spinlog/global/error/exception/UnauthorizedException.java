package com.example.spinlog.global.error.exception;

public class UnauthorizedException extends BusinessException {

    private String value;

    public UnauthorizedException(String value) {
        this(value, ErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String value, ErrorCode errorCode) {
        super(value, errorCode);
        this.value = value;
    }
}
