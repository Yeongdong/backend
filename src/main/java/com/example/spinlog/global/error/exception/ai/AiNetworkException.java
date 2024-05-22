package com.example.spinlog.global.error.exception.ai;

import com.example.spinlog.global.error.exception.CustomApiException;
import com.example.spinlog.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AiNetworkException extends CustomApiException {

    private final String value;

    public AiNetworkException(String value) {
        this(value, ErrorCode.AI_NETWORK);
    }

    public AiNetworkException(String value, ErrorCode errorCode) {
        super(value, errorCode);
        this.value = value;
    }
}
