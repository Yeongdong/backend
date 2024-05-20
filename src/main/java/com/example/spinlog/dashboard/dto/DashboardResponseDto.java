package com.example.spinlog.dashboard.dto;

import com.example.spinlog.utils.NullDataConverter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class DashboardResponseDto {
    private Float satisfactionAverage;
    private List<EmotionAmount> emotionAmountTotal;
    private List<DailyAmount> dailyAmount;

    @Builder
    public DashboardResponseDto(Float satisfactionAverage, List<EmotionAmount> emotionAmountTotal, List<DailyAmount> dailyAmount) {
        this.satisfactionAverage = NullDataConverter.convertFloat(satisfactionAverage);
        this.emotionAmountTotal = NullDataConverter.convertList(emotionAmountTotal);
        this.dailyAmount = NullDataConverter.convertList(dailyAmount);
    }
}
