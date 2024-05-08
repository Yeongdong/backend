package com.example.spinlog.statistics.controller.dto;

import com.example.spinlog.statistics.entity.MBTIFactor;
import com.example.spinlog.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.spinlog.user.entity.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GenderDailyAmountSumResponse {
    private Gender gender;
    private List<EmotionAmountSum> emotionAmountSums;

    public static GenderDailyAmountSumResponse of(Gender gender, List<MBTIDailyAmountSumDto> dtos){
        return GenderDailyAmountSumResponse.builder()
                .gender(gender)
                .emotionAmountSums(
                        dtos.stream()
                                .map(EmotionAmountSum::of)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public String toString() {
        return "EmotionAmountAverage\n" +
                "gender=" + gender + "\n" +
                "emotionCount=\n" +
                emotionAmountSums.stream()
                        .map(EmotionAmountSum::toString)
                        .map(ea -> ea+"\n")
                        .toList()
                + "\n";
    }

    @Getter
    @ToString
    public static class EmotionAmountSum {
        private LocalDate date;
        private Long amountSum;

        @Builder
        public EmotionAmountSum(LocalDate date, Long amountSum) {
            this.date = date;
            this.amountSum = amountSum;
        }

        public static EmotionAmountSum of(MBTIDailyAmountSumDto dto){
            return EmotionAmountSum.builder()
                    .date(dto.getLocalDate())
                    .amountSum(dto.getAmountSum())
                    .build();
        }
    }
}
