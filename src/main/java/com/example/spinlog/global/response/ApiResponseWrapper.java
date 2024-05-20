package com.example.spinlog.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ApiResponseWrapper<T> {
    private boolean success;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    private List<ErrorResponse> errors;

    @Builder
    private ApiResponseWrapper(boolean success, String message, T data, List<ErrorResponse> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }
}
