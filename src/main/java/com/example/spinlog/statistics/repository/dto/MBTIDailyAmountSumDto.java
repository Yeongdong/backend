package com.example.spinlog.statistics.repository.dto;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MBTIDailyAmountSumDto {
    private String mbtiFactor;
    private LocalDate localDate;
    private Long amountSum;
}
