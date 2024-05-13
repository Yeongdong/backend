package com.example.spinlog.global.exception;

import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * NullPointerException 예외 처리
     * 해당 예외가 발생하면 HTTP 400 Bad Request 상태 코드 반환
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseWrapper<Void> handleNullPointerException(NullPointerException e) {
        return ResponseUtils.error("Requested resource is null");
    }

    /**
     * NoSuchElementException 예외 처리
     * 해당 예외가 발생하면 HTTP 404 Not Found 상태 코드 반환
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseWrapper<Void> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseUtils.error("Requested resource not found");
    }

    /**
     * IllegalArgumentException 예외 처리
     * 해당 예외가 발생하면 HTTP 400 Bad Request 상태 코드 반환
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseWrapper<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseUtils.error("Invalid input");
    }

    /**
     * 모든 예외를 포괄적으로 처리하는 메서드
     * 예외가 발생하면 HTTP 500 Internal Server Error 상태 코드 반환
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponseWrapper<Void> handleGenericException(Exception e) {
        log.error(e.getMessage());
        return ResponseUtils.error("An unexpected error occurred");
    }
}
