package com.example.spinlog.calendar.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class DailyCalendarResponseDto {
    private List<DaySpend> daySpendList;

    @Builder
    public DailyCalendarResponseDto(List<DaySpend> daySpendList) {
        this.daySpendList = (daySpendList != null ? daySpendList : Collections.emptyList());
    }
}
