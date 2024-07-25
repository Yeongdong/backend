package com.example.spinlog.calendar.dto;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DaySpend {
    private Long articleId;
    private RegisterType registerType;
    private Integer amount;
    private String content;
    private Float satisfaction;
    private Emotion emotion;
}
