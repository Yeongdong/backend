package com.example.spinlog.global.error.exception.user;

import com.example.spinlog.global.error.exception.ErrorCode;
import com.example.spinlog.global.error.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String id) {
        super(id, ErrorCode.USER_NOT_FOUND);
    }
}
