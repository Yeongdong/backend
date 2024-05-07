package com.example.spinlog.global.exception.codes;

import lombok.Getter;

@Getter
public enum ErrorCode {
    /**
     * Global Error
     */
    // 잘못된 서버 요청
    BAD_REQUEST_ERROR("Bad Request Exception"),

    // @RequestBody 데이터 미존재
    REQUEST_BODY_MISSING_ERROR("Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE("Invalid Type Value"),

    // 권한이 없음
    FORBIDDEN_ERROR("Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR("Not Found Exception"),

    // NULL Pointer Exception 발생
    NULL_POINTER_ERROR("Null Pointer Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    INVALID_INPUT_VALUE("handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR("Header에 데이터가 존재하지 않는 경우 "),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR("Internal Server Error Exception"),

    /**
     * Custom Error
     */
    CREATE_ERROR("Create Error"),
    READ_ERROR("Read Error"),
    UPDATE_ERROR("Update Error"),
    DELETE_ERROR("Delete Error"),;

    private String message;


    ErrorCode(String message) {
    }
}
