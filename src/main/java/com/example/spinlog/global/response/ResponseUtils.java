package com.example.spinlog.global.response;

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

    // 에러 발생
    public static <T> ApiResponseWrapper<T> error(String message) {
        return ApiResponseWrapper.<T>builder()
                .success(false)
                .message(message)
//                .data(requestDTO)
                .build();
    }

    public static <T> ApiResponseWrapper<T> error(String message, T response) {
        return ApiResponseWrapper.<T>builder()
                .success(false)
                .message(message)
                .data(response)
                .build();
    }
}
