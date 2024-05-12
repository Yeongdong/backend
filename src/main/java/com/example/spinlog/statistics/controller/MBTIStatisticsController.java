package com.example.spinlog.statistics.controller;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
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
    public ApiResponseWrapper<MBTIEmotionAmountAverageResponse> getAmountAverageEachMBTIAndEmotionLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return ResponseUtils.ok(
                statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(
                        LocalDate.now(),
                        RegisterType.valueOf(registerType)),
                "MBTI별 감정별 금액 평균");
    }

    @GetMapping("/api/statistics/mbti/daily/amounts/sum")
    public ApiResponseWrapper<MBTIDailyAmountSumResponse> getAmountSumsEachMBTIAndDayLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType) {
        return ResponseUtils.ok(
                statisticsService.getAmountSumsEachMBTIAndDayLast90Days(
                        LocalDate.now(),
                        RegisterType.valueOf(registerType)),
                "MBTI별 일별 금액 총합");
    }

    @GetMapping("/api/statistics/mbti/word/frequencies")
    public ApiResponseWrapper<MBTIWordFrequencyResponse> getWordFrequencyLast90Days(){
        return ResponseUtils.ok(
                statisticsService
                        .getWordFrequenciesLast90Days(LocalDate.now()),
                "MBTI별 단어 빈도수");
    }

    @GetMapping("/api/statistics/mbti/satisfactions/average")
    public ApiResponseWrapper<MBTISatisfactionAverageResponse> getSatisfactionAveragesEachMBTILast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return ResponseUtils.ok(
                statisticsService.getSatisfactionAveragesEachMBTILast90Days(
                        LocalDate.now(),
                        RegisterType.valueOf(registerType)),
                "MBTI별 만족도 평균");
    }
}
