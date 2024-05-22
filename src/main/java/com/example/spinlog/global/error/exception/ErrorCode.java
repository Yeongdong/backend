package com.example.spinlog.global.error.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // common
    INVALID_INPUT_VALUE("Invalid Input Value"),
    METHOD_NOT_ALLOWED("Method Not Allowed"),
    INTERNAL_SERVER_ERROR("Server Error"),
    INVALID_TYPE_VALUE("Invalid Type Value"),
    HANDLE_ACCESS_DENIED("Access is Denied"),
    NULL_POINTER("Null Pointer"),
    BAD_REQUEST("Bad Request"),

    // business
    NOT_FOUND("Entity not found"),
    UNAUTHORIZED("Unauthorized"),
    AI_NETWORK("AI Network Error"),

    // article
    ARTICLE_NOT_FOUND("Article not found"),
    UNAUTHORIZED_ARTICLE_REQUEST("Unauthorized Article Request"),

    // ai
    EMPTY_COMMENT("AI Comment is empty"),

    // user
    USER_NOT_FOUND("User not found"), ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
