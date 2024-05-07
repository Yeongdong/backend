package com.example.spinlog.global.response;

import com.example.spinlog.global.exception.codes.ErrorCode;

import java.util.Collections;
import java.util.List;

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
    public static <T> ApiResponseWrapper<T> error(ErrorCode errorCode, ErrorResponse errorResponse) {
        return ApiResponseWrapper.<T>builder()
                .success(false)
                .message(errorCode.getMessage())
                .data(null)
                .errors(List.of(errorResponse))
                .build();
    }

    public static <T> ApiResponseWrapper<T> error(ErrorCode errorCode) {
        return ApiResponseWrapper.<T>builder()
                .success(false)
                .message(errorCode.getMessage())
                .data(null)
                .errors(Collections.emptyList())
                .build();
    }
}
