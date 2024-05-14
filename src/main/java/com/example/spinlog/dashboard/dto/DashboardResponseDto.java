package com.example.spinlog.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponseDto {
    private Float satisfactionAverage;
    private List<EmotionAmount> emotionAmountTotal;
    private List<DailyAmount> dailyAmount;
}
