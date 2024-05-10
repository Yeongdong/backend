package com.example.spinlog.article.dto;

import lombok.Data;

import java.util.List;

@Data
public class MainPageDayResponseDto {
    private List<DaySpendItemList> daySpendList;
}
