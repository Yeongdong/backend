package com.example.spinlog.global.exception.codes;

import lombok.Getter;

@Getter
public enum SuccessCode {
    /**
     * Success code
     */
    CREATE_SUCCESS("Create success"),
    READ_SUCCESS("Read success"),
    UPDATE_SUCCESS("Update success"),
    DELETE_SUCCESS("Delete success");

    private SuccessCode(String message) {}
}
