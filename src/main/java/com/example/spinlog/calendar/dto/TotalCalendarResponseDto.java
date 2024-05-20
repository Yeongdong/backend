package com.example.spinlog.calendar.dto;

import com.example.spinlog.utils.NullDataConverter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TotalCalendarResponseDto {
    private Budget budget;
    private List<MonthSpend> monthSpendList;
    private List<DaySpend> daySpendList;

    @Builder
    public TotalCalendarResponseDto(Budget budget, List<MonthSpend> monthSpendList, List<DaySpend> daySpendList) {
        this.budget = budget;
        this.monthSpendList = NullDataConverter.convertList(monthSpendList);
        this.daySpendList = NullDataConverter.convertList(daySpendList);
    }
}
