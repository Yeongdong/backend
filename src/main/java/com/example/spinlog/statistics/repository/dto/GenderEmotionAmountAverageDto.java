package com.example.spinlog.statistics.repository.dto;

import com.example.spinlog.user.entity.Gender;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class GenderEmotionAmountAverageDto {
    private Gender gender;
    private String emotion;
    private Long amountAverage;
}
