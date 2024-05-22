package com.example.spinlog.global.response;

import com.example.spinlog.global.error.ErrorResponse;

public class ResponseUtils {

    // 요청 성공 => 응답 데이터 O
    public static <T> ApiResponseWrapper<T> ok(T response, String message) {
        return ApiResponseWrapper.<T>builder()
                .success(true)
                .message(message)
                .data(response)
                .build();
    }

    // 요청 성공 => 응답 데이터 X
    public static <T> ApiResponseWrapper<T> ok(String message) {
        return ApiResponseWrapper.<T>builder()
                .success(true)
                .message(message)
                .data(null)
                .build();
    }

    // 에러 발생 => 임시
    public static <T> ApiResponseWrapper<T> error(String message) {
        return ApiResponseWrapper.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponseWrapper<T> error(String message, T response) {
        return ApiResponseWrapper.<T>builder()
                .success(false)
                .message(message)
                .data(response)
                .build();
    }

    // 예외 처리된 결과 반환 메서드
    public static ApiResponseWrapper<ErrorResponse> error(ErrorResponse response) {
        return ApiResponseWrapper.<ErrorResponse>builder()
                .success(false)
                .errorResponse(response)
                .build();
    }
}
