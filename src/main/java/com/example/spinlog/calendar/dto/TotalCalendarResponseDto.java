package com.example.spinlog.calendar.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TotalCalendarResponseDto {
    private Budget budget;
    private List<MonthSpend> monthSpendList;
    private List<DaySpend> daySpendList;
}
