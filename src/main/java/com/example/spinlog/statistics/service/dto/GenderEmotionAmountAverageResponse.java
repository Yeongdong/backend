package com.example.spinlog.statistics.service.dto;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.statistics.repository.dto.GenderEmotionAmountAverageDto;
import com.example.spinlog.user.entity.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GenderEmotionAmountAverageResponse {
    private Gender gender;
    private List<EmotionAmountAverage> emotionAmountAverages;

    public static GenderEmotionAmountAverageResponse of(Gender gender, List<GenderEmotionAmountAverageDto> dtos){
        return GenderEmotionAmountAverageResponse.builder()
                .gender(gender)
                .emotionAmountAverages(
                        dtos.stream()
                                .map(EmotionAmountAverage::of)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Getter
    @Builder
    public static class EmotionAmountAverage {
        private Emotion emotion;
        private Long amountAverage;

        public static EmotionAmountAverage of(GenderEmotionAmountAverageDto dto){
            return EmotionAmountAverage.builder()
                    .emotion(Emotion.valueOf(dto.getEmotion()))
                    .amountAverage(dto.getAmountAverage())
                    .build();
        }
    }
}
