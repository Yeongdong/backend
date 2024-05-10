package com.example.spinlog.article.dto;

import lombok.Data;

import java.util.List;

@Data
public class MainPageTotalResponseDto {
    private Budget budget;
    private List<DaySpend> monthSpendList;
    private List<DaySpendItemList> daySpendList;
}
