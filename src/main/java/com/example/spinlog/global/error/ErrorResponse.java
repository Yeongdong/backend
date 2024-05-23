package com.example.spinlog.global.error;

import com.example.spinlog.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String message; // Error message
    private final String type; // Error type

    public ErrorResponse(String message, ErrorCode type) {
        this.message = message;
        this.type = type.name();
    }

    public static ErrorResponse of(String message, ErrorCode type) {
        return new ErrorResponse(message, type);
    }
}
