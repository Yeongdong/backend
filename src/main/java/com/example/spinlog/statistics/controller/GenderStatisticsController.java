package com.example.spinlog.statistics.controller;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.controller.dto.GenderDailyAmountSumResponse;
import com.example.spinlog.statistics.controller.dto.GenderEmotionAmountAverageResponse;
import com.example.spinlog.statistics.controller.dto.GenderWordFrequencyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GenderStatisticsController {
    @GetMapping("/api/statistics/gender/emotion/amounts/average")
    public List<GenderEmotionAmountAverageResponse> getAmountAveragesEachGenderAndEmotionLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return null;
    }

    @GetMapping("/api/statistics/gender/daily/amounts/sum")
    public List<GenderDailyAmountSumResponse> getAmountSumsEachGenderAndDayLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return null;
    }

    @GetMapping("/api/statistics/gender/word/frequencies")
    public List<GenderWordFrequencyResponse> getWordFrequencyEachGenderLast90Days(){
        return null;
    }

    @GetMapping("/api/statistics/gender/statisfactions/average")
    public List<Object> getStatisfactionAveragesEachGenderLast90Days(
            @RequestParam(defaultValue = "SPEND") String registerType){
        return null;
    }
}
