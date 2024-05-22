package com.example.spinlog.global.error;

import com.example.spinlog.global.error.exception.*;
import com.example.spinlog.global.error.exception.ai.AiNetworkException;
import com.example.spinlog.global.error.exception.UnauthorizedException;
import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // common error handler

    /**
     * Valid 바인딩 에러시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseWrapper<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Handle MethodArgumentNotValidException", e);

        int status = HttpStatus.BAD_REQUEST.value();
        ErrorResponse response = ErrorResponse.of(status, ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());

        return ResponseUtils.error(response);
    }

    /**
     * Request Param 타입 미스매치
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseWrapper<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Handle MethodArgumentTypeMismatchException", e);

        int status = HttpStatus.BAD_REQUEST.value();
        ErrorResponse response = ErrorResponse.of(status, ErrorCode.INVALID_INPUT_VALUE, e.getMessage());

        return ResponseUtils.error(response);
    }

    /**
     * 지원하지 않는 HTTP 메서드로 요청 시
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ApiResponseWrapper<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.info("Handle HttpRequestMethodNotSupportedException", e);

        final int status = HttpStatus.METHOD_NOT_ALLOWED.value();
        final ErrorResponse response = ErrorResponse.of(status, ErrorCode.METHOD_NOT_ALLOWED);

        return ResponseUtils.error(response);
    }

    /**
     * Null Pointer
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseWrapper<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.warn("Handle NullPointerException", e);

        int status = HttpStatus.NOT_FOUND.value();
        ErrorResponse response = ErrorResponse.of(status, ErrorCode.NULL_POINTER, e.getMessage());
        return ResponseUtils.error(response);
    }

    // business error handler

    /**
     * 조회한 객체를 찾지 못할시 발생
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseWrapper<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.warn("Handle NotFoundException", e);

        ErrorCode errorCode = e.getErrorCode();
        String value = e.getValue();
        int status = HttpStatus.NOT_FOUND.value();
        ErrorResponse response = ErrorResponse.of(status, errorCode, value);

        return ResponseUtils.error(response);
    }

    /**
     * 유저의 권한이 없을시 발생
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponseWrapper<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("Handle UnauthorizedException", e);

        ErrorCode errorCode = e.getErrorCode();
        String value = e.getMessage();
        int status = HttpStatus.UNAUTHORIZED.value();
        ErrorResponse response = ErrorResponse.of(status, errorCode, value);

        return ResponseUtils.error(response);
    }

    @ExceptionHandler(AiNetworkException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiResponseWrapper<ErrorResponse> handleAiNetworkException(AiNetworkException e) {
        log.error("Handle AiNetworkException", e);

        ErrorCode errorCode = e.getErrorCode();
        int status = HttpStatus.SERVICE_UNAVAILABLE.value();
        String message = e.getMessage();
        ErrorResponse response = ErrorResponse.of(status, errorCode, message);

        return ResponseUtils.error(response);

    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseWrapper<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("Handle BusinessException", e);

        ErrorCode errorCode = e.getErrorCode();
        int status = HttpStatus.BAD_GATEWAY.value();
        ErrorResponse response = ErrorResponse.of(status, errorCode);

        return ResponseUtils.error(response);
    }

    /**
     * 모든 예외를 포괄적으로 처리하는 메서드
     * 예외가 발생하면 HTTP 500 Internal Server Error 상태 코드 반환
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponseWrapper<ErrorResponse> handleException(Exception e) {
        log.warn("Handle Exception", e);

        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        ErrorResponse response = ErrorResponse.of(status, ErrorCode.INTERNAL_SERVER_ERROR);

        return ResponseUtils.error(response);
    }

    //======================================하위 코드 삭제 예정=================================================

//    /**
//     * NullPointerException 예외 처리
//     * 해당 예외가 발생하면 HTTP 400 Bad Request 상태 코드 반환
//     */
//    @ExceptionHandler(NullPointerException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiResponseWrapper<Void> handleNullPointerException(NullPointerException e) {
//        return ResponseUtils.error("Requested resource is null");
//    }

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
}
