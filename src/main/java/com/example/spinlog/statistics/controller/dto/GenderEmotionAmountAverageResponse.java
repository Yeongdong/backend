package com.example.spinlog.statistics.controller.dto;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import com.example.spinlog.user.entity.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GenderEmotionAmountAverageResponse {
    private Gender gender;
    private List<EmotionAmountAverage> emotionAmountAverages;

    @Getter
    @Builder
    public static class EmotionAmountAverage {
        private Emotion emotion;
        private Long amountAverage;
    }

    public static EmotionAmountAverage of(MBTIEmotionAmountAverageDto dto){
        return EmotionAmountAverage.builder()
                .emotion(Emotion.valueOf(dto.getEmotion()))
                .amountAverage(dto.getAmountAverage())
                .build();
    }
}
