package com.example.spinlog.statistics.service;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.controller.dto.*;
import com.example.spinlog.statistics.repository.GenderStatisticsRepository;
import com.example.spinlog.statistics.repository.dto.*;
import com.example.spinlog.statistics.required_have_to_delete.UserInfoService;
import com.example.spinlog.user.entity.Mbti;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@Transactional(readOnly = true)
public class GenderStatisticsService {
    private final GenderStatisticsRepository genderStatisticsRepository;
    private final WordExtractionService wordExtractionService;
    private final int PERIOD_CRITERIA = 90;

    public GenderStatisticsService(GenderStatisticsRepository genderStatisticsRepository, WordExtractionService wordExtractionService) {
        this.genderStatisticsRepository = genderStatisticsRepository;
        this.wordExtractionService = wordExtractionService;
    }

    public List<GenderEmotionAmountAverageResponse> getAmountAveragesEachGenderAndEmotionLast90Days(LocalDate today, RegisterType registerType){
        // 레포에게 성별, 감정별 금액평균 데이터 요청
        // 그루핑
        return null;
    }

    public List<GenderDailyAmountSumResponse> getAmountSumsEachGenderAndDayLast90Days(LocalDate today, RegisterType registerType) {
        // 레포에게 성별, 일별 금액평균 데이터 요청
        // 그루핑
        return null;
    }

    public GenderWordFrequencyResponse getWordFrequenciesLast90Days(LocalDate today){
        // 레포에게 성별 메모 요청
        // wordExtractionService에게 명령
        // 정리해서 반환
        return null;
    }

    private static boolean isNone(Mbti mbti) {
        return mbti == null || mbti == Mbti.NONE;
    }

    public List<GenderSatisfactionAverageDto> getSatisfactionAveragesEachGenderLast90Days(LocalDate today, RegisterType registerType){
        // 레포에게 성별 만족도 평균 요청
        // 그대로 반환
        return null;
    }
}
