package com.example.spinlog.global.response;

import com.example.spinlog.global.exception.codes.ErrorCode;
import lombok.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;

@Getter
@Builder
public class ErrorResponse {
    private String field;
    private Object value;
    private String reason;

    public static ErrorResponse of(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return ErrorResponse.builder()
                                .field(fieldError.getField())
                                .value(fieldError.getRejectedValue())
                                .reason(fieldError.getDefaultMessage())
                                .build();
                    }
                    return ErrorResponse.builder()
                            .field(error.getObjectName())
                            .value(null)
                            .reason(error.getDefaultMessage())
                            .build();
                }).findFirst()
                .orElse(null);
    }

    public static ErrorResponse of() {
        return ErrorResponse.builder()
                .field("")
                .value(null)
                .reason("")
                .build();
    }
}
