package com.example.spinlog.global.exception;

import com.example.spinlog.global.exception.codes.ErrorCode;
import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ErrorResponse;
import com.example.spinlog.global.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 유효성 검사에 실패시 발생하는 예외를 처리하는 메서드
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        ErrorResponse response = ErrorResponse.of(e.getBindingResult());
        return ResponseEntity.badRequest().body(ResponseUtils.error(ErrorCode.INVALID_INPUT_VALUE, response));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponseWrapper<ErrorResponse>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException", e);
        return ResponseEntity.badRequest().body(ResponseUtils.error(ErrorCode.INVALID_TYPE_VALUE));
    }

    /**
     * 모든 예외를 포괄적으로 처리하는 메서드
     * 예외가 발생하면 HTTP 500 Internal Server Error 상태 코드 반환
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public <T> ResponseEntity<ApiResponseWrapper<ErrorResponse>> handleGenericException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
