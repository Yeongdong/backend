package com.example.spinlog.calendar.repository.dto;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MonthSpendDto {
    private Long articleId;
    private RegisterType registerType;
    private Integer amount;
    private String content;
    private Float satisfaction;
    private Emotion emotion;
    private LocalDateTime spendDate;
}
