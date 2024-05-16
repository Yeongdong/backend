package com.example.spinlog.calendar.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthSpend {
    private String date;
    private Integer daySpend;
    private Integer daySave;
}
