package com.example.spinlog.statistics.controller;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.service.dto.MBTIDailyAmountSumResponse;
import com.example.spinlog.statistics.service.dto.MBTIEmotionAmountAverageResponse;
import com.example.spinlog.statistics.service.dto.MBTISatisfactionAverageResponse;
import com.example.spinlog.statistics.service.dto.MBTIWordFrequencyResponse;
import com.example.spinlog.statistics.repository.dto.MBTISatisfactionAverageDto;
import com.example.spinlog.statistics.service.MBTIStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MBTIStatisticsController {
    private final MBTIStatisticsService statisticsService;

    @GetMapping("/api/statistics/mbti/emotion/amounts/average")
    public MBTIEmotionAmountAverageResponse getAmountAverageEachMBTIAndEmotionLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }

    @GetMapping("/api/statistics/mbti/daily/amounts/sum")
    public MBTIDailyAmountSumResponse getAmountSumsEachMBTIAndDayLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType) {
        return statisticsService.getAmountSumsEachMBTIAndDayLast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }

    @GetMapping("/api/statistics/mbti/word/frequencies")
    public MBTIWordFrequencyResponse getWordFrequencyLast90Days(){
        return statisticsService.getWordFrequenciesLast90Days(LocalDate.now());
    }

    @GetMapping("/api/statistics/mbti/satisfactions/average")
    public MBTISatisfactionAverageResponse getSatisfactionAveragesEachMBTILast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return statisticsService.getSatisfactionAveragesEachMBTILast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }
}
