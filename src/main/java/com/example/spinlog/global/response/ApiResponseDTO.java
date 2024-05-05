package com.example.spinlog.global.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.ErrorResponse;

@Getter
public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    @Builder
    private ApiResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
