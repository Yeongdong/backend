package com.example.spinlog.global.error;

import com.example.spinlog.global.error.exception.ErrorCode;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {
    private int status;     // Http Status
    private String message; // Error message
    private List<String> values = new ArrayList<>(); // Error causing value

    private ErrorResponse(final int status, final ErrorCode errorCode) {
        this.status = status;
        this.message = errorCode.getMessage();
    }

    private ErrorResponse(final int status, final ErrorCode errorCode, final String value) {
        this(status, errorCode);
        this.values = List.of(value);
    }

    private ErrorResponse(final int status, final ErrorCode errorCode, final List<String> values) {
        this(status, errorCode);
        this.values = values;
    }

    public static ErrorResponse of(int status, ErrorCode errorCode, BindingResult bindingResult) {
        List<String> values = bindingResult.getFieldErrors().stream()
                .map(error -> error.getRejectedValue() == null ? "" : error.getRejectedValue().toString())
                .toList();
        return new ErrorResponse(status, errorCode, values);
    }

    public static ErrorResponse of(int status, ErrorCode errorCode, String value) {
        return new ErrorResponse(status, errorCode, value);
    }

    public static ErrorResponse of(int status, ErrorCode errorCode) {
        return new ErrorResponse(status, errorCode);
    }
}
