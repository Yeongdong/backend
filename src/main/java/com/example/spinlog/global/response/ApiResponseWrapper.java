package com.example.spinlog.global.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;

    @Builder
    private ApiResponseWrapper(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
