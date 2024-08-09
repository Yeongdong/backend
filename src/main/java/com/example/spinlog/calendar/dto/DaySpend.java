package com.example.spinlog.calendar.dto;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.calendar.repository.dto.MonthSpendDto;
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

    public static DaySpend of(MonthSpendDto dto){
        return DaySpend.builder()
                .articleId(dto.getArticleId())
                .registerType(dto.getRegisterType())
                .amount(dto.getAmount())
                .content(dto.getContent())
                .satisfaction(dto.getSatisfaction())
                .emotion(dto.getEmotion())
                .build();
    }
}
