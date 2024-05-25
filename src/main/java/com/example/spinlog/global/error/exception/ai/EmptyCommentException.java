package com.example.spinlog.global.error.exception.ai;

import com.example.spinlog.global.error.exception.ErrorCode;

public class EmptyCommentException extends AiNetworkException {
    public EmptyCommentException(final String message) {
        super(message, ErrorCode.EMPTY_COMMENT);
    }
}
