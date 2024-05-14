package com.example.spinlog.calendar.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DailyCalendarResponseDto {
    private List<DaySpend> daySpendList;
}
