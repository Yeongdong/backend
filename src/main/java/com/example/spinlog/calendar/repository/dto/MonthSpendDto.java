package com.example.spinlog.calendar.repository.dto;

import com.example.spinlog.article.entity.RegisterType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MonthSpendDto {
    private LocalDateTime spendDate;
    private Integer amount;
    private RegisterType registerType;
}
