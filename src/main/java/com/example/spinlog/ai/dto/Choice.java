package com.example.spinlog.ai.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Choice implements Serializable {
    private Integer index;
    private Message message;
}
