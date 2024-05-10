package com.example.spinlog.statistics.controller;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.repository.dto.GenderSatisfactionAverageDto;
import com.example.spinlog.statistics.service.GenderStatisticsService;
import com.example.spinlog.statistics.service.dto.GenderDailyAmountSumResponse;
import com.example.spinlog.statistics.service.dto.GenderEmotionAmountAverageResponse;
import com.example.spinlog.statistics.service.dto.GenderWordFrequencyResponse;
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
public class GenderStatisticsController {
    private final GenderStatisticsService genderStatisticsService;
    @GetMapping("/api/statistics/gender/emotion/amounts/average")
    public List<GenderEmotionAmountAverageResponse> getAmountAveragesEachGenderAndEmotionLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return genderStatisticsService.getAmountAveragesEachGenderAndEmotionLast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }

    @GetMapping("/api/statistics/gender/daily/amounts/sum")
    public List<GenderDailyAmountSumResponse> getAmountSumsEachGenderAndDayLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return genderStatisticsService.getAmountSumsEachGenderAndDayLast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }

    @GetMapping("/api/statistics/gender/word/frequencies")
    public GenderWordFrequencyResponse getWordFrequencyEachGenderLast90Days(){
        return genderStatisticsService.getWordFrequenciesEachGenderLast90Days(LocalDate.now());
    }

    @GetMapping("/api/statistics/gender/satisfactions/average")
    public List<GenderSatisfactionAverageDto> getSatisfactionAveragesEachGenderLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return genderStatisticsService.getSatisfactionAveragesEachGenderLast90Days(
                LocalDate.now(),
                RegisterType.valueOf(registerType));
    }
}
