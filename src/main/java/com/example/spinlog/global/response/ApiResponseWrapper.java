package com.example.spinlog.global.response;

import com.example.spinlog.global.error.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;
    private ErrorResponse errorResponse;
}
