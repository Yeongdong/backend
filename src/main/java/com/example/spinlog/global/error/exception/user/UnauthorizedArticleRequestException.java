package com.example.spinlog.global.error.exception.user;

import com.example.spinlog.global.error.exception.ErrorCode;
import com.example.spinlog.global.error.exception.UnauthorizedException;

public class UnauthorizedArticleRequestException extends UnauthorizedException {

    public UnauthorizedArticleRequestException(String message) {
        super(message, ErrorCode.UNAUTHORIZED_ARTICLE_REQUEST);
    }
}
