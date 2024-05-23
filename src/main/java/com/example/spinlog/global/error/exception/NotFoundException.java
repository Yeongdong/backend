package com.example.spinlog.global.error.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends BusinessException {

    private final String value;

    public NotFoundException(String value) {
        this(value, ErrorCode.NOT_FOUND);
    }

    public NotFoundException(String value, ErrorCode errorCode) {
        super(value, errorCode);
        this.value = value;
    }
}
