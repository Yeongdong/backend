package com.example.spinlog.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class DailyAmount {
    private String date;
    private Integer amount;

    public static DailyAmount of(LocalDate key, Integer value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return new DailyAmount(key.format(formatter), value);
    }
}
