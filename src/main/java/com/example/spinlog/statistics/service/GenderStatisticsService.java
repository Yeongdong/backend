package com.example.spinlog.statistics.service;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.repository.GenderStatisticsRepository;
import com.example.spinlog.statistics.repository.dto.*;
import com.example.spinlog.statistics.service.dto.GenderDailyAmountSumResponse;
import com.example.spinlog.statistics.service.dto.GenderEmotionAmountAverageResponse;
import com.example.spinlog.statistics.service.dto.GenderWordFrequencyResponse;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        List<GenderEmotionAmountAverageDto> dtos = genderStatisticsRepository.
                getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(registerType, startDate, today);

        return dtos.stream()
                .collect(
                        groupingBy(GenderEmotionAmountAverageDto::getGender))
                .entrySet().stream()
                .map((e) ->
                        GenderEmotionAmountAverageResponse.of(e.getKey(), e.getValue()))
                .toList();
    }

    public List<GenderDailyAmountSumResponse> getAmountSumsEachGenderAndDayLast90Days(LocalDate today, RegisterType registerType) {
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        List<GenderDailyAmountSumDto> dtos = genderStatisticsRepository
                .getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(registerType, startDate, today);

        return dtos.stream()
                .collect(
                        groupingBy(GenderDailyAmountSumDto::getGender))
                .entrySet().stream()
                .map((e) ->
                        GenderDailyAmountSumResponse.of(e.getKey(), e.getValue()))
                .toList();
    }

    public GenderWordFrequencyResponse getWordFrequenciesEachGenderLast90Days(LocalDate today){
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        List<MemoDto> maleMemos = genderStatisticsRepository.getAllMemosByGenderBetweenStartDateAndEndDate(Gender.MALE, startDate, today);
        List<MemoDto> femaleMemos = genderStatisticsRepository.getAllMemosByGenderBetweenStartDateAndEndDate(Gender.FEMALE, startDate, today);

        return GenderWordFrequencyResponse.builder()
                .maleWordFrequencies(
                        wordExtractionService.analyzeWords(
                                maleMemos.stream()
                                        .flatMap((m) -> Stream.of(
                                                m.getContent(),
                                                m.getEvent(),
                                                m.getReason(),
                                                m.getThought(),
                                                m.getImprovements()))
                                        .filter(Objects::nonNull)
                                        .toList()))
                .femaleWordFrequencies(
                        wordExtractionService.analyzeWords(
                                femaleMemos.stream()
                                        .flatMap((m) -> Stream.of(
                                                m.getContent(),
                                                m.getEvent(),
                                                m.getReason(),
                                                m.getThought(),
                                                m.getImprovements()))
                                        .filter(Objects::nonNull)
                                        .toList()))
                .build();
    }

    private static boolean isNone(Mbti mbti) {
        return mbti == null || mbti == Mbti.NONE;
    }

    public List<GenderSatisfactionAverageDto> getSatisfactionAveragesEachGenderLast90Days(LocalDate today, RegisterType registerType){
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        return genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(registerType, startDate, today);
    }
}
