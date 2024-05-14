package com.example.spinlog.calendar.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DaySpend {
    private Long articleId;
    private String registerType;
    private Integer amount;
    private String content;
    private Float satisfaction;
    private String emotion;
}
