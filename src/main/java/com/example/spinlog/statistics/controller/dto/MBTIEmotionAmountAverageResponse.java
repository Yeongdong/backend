package com.example.spinlog.statistics.controller.dto;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.statistics.entity.MBTIFactor;
import com.example.spinlog.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MBTIEmotionAmountAverageResponse {
        private MBTIFactor mbtiFactor;
        private List<EmotionAmountAverage> emotionAmountAverages;

        public static MBTIEmotionAmountAverageResponse of(String factor, List<MBTIEmotionAmountAverageDto> dtos){
            return MBTIEmotionAmountAverageResponse.builder()
                    .mbtiFactor(MBTIFactor.valueOf(factor))
                    .emotionAmountAverages(
                            dtos.stream()
                                    .map(EmotionAmountAverage::of)
                                    .collect(Collectors.toList())
                    )
                    .build();
        }

        @Override
        public String toString() {
            return "EmotionAmountAverage\n" +
                    "mbtiFactor =" + mbtiFactor + "\n" +
                    "emotionCount=\n" +
                    emotionAmountAverages.stream()
                            .map(EmotionAmountAverage::toString)
                            .map(ea -> ea+"\n")
                            .toList()
                    + "\n";
        }

    @Getter
    @ToString
    public static class EmotionAmountAverage {
        private Emotion emotion;
        private Long amountAverage;

        @Builder
        public EmotionAmountAverage(Emotion emotion, Long average) {
            this.emotion = emotion;
            this.amountAverage = average;
        }

        public static EmotionAmountAverage of(MBTIEmotionAmountAverageDto dto){
            return EmotionAmountAverage.builder()
                    .emotion(Emotion.valueOf(dto.getEmotion()))
                    .average(dto.getAmountAverage())
                    .build();
        }
    }
}
