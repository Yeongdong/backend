package com.example.spinlog.calendar.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthSpend {
    private LocalDate date;
    private Integer daySpend;
    private Integer daySave;
}
