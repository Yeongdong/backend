package com.example.spinlog.statistics.service;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.service.dto.MBTIDailyAmountSumResponse;
import com.example.spinlog.statistics.service.dto.MBTIEmotionAmountAverageResponse;
import com.example.spinlog.statistics.service.dto.MBTISatisfactionAverageResponse;
import com.example.spinlog.statistics.service.dto.MBTIWordFrequencyResponse;
import com.example.spinlog.statistics.repository.MBTIStatisticsRepository;
import com.example.spinlog.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.spinlog.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import com.example.spinlog.statistics.repository.dto.MBTISatisfactionAverageDto;
import com.example.spinlog.statistics.repository.dto.MemoDto;
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
public class MBTIStatisticsService {
    private final MBTIStatisticsRepository mbtiStatisticsRepository;
    private final WordExtractionService wordExtractionService;
    private final UserInfoService userInfoService;
    private final int PERIOD_CRITERIA = 90;

    public MBTIStatisticsService(MBTIStatisticsRepository mbtiStatisticsRepository, WordExtractionService wordExtractionService, UserInfoService userInfoService) {
        this.mbtiStatisticsRepository = mbtiStatisticsRepository;
        this.wordExtractionService = wordExtractionService;
        this.userInfoService = userInfoService;
    }

    public MBTIEmotionAmountAverageResponse getAmountAveragesEachMBTIAndEmotionLast90Days(LocalDate today, RegisterType registerType){
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        List<MBTIEmotionAmountAverageDto> dtos = mbtiStatisticsRepository.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(registerType, startDate, today);
        
        // TODO 없는 값에 대한 0 padding 작업

        return MBTIEmotionAmountAverageResponse.builder()
                .mbti(userInfoService.getUserMBTI())
                .mbtiEmotionAmountAverages(
                        dtos.stream()
                                .collect(
                                        groupingBy(MBTIEmotionAmountAverageDto::getMbtiFactor))
                                .entrySet().stream()
                                .map((e) ->
                                        MBTIEmotionAmountAverageResponse.MBTIEmotionAmountAverage.of(e.getKey(), e.getValue()))
                                .toList())
                .build();
    }

    public MBTIDailyAmountSumResponse getAmountSumsEachMBTIAndDayLast90Days(LocalDate today, RegisterType registerType) {
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        List<MBTIDailyAmountSumDto> dtos = mbtiStatisticsRepository.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(registerType, startDate, today);

        // TODO 없는 값에 대한 0 padding 작업

        return MBTIDailyAmountSumResponse.builder()
                .mbti(userInfoService.getUserMBTI())
                .mbtiDailyAmountSums(
                        dtos.stream()
                                .collect(
                                        groupingBy(MBTIDailyAmountSumDto::getMbtiFactor))
                                .entrySet().stream()
                                .map((e) ->
                                        MBTIDailyAmountSumResponse.MBTIDailyAmountSum.of(e.getKey(), e.getValue()))
                                .toList())
                .build();
    }

    public MBTIWordFrequencyResponse getWordFrequenciesLast90Days(LocalDate today){
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        // 최근 90일동안 모든 유저가 적은 메모의 빈도수 측정
        List<MemoDto> memos = mbtiStatisticsRepository.getAllMemosByMBTIBetweenStartDateAndEndDate(Mbti.NONE.toString(), startDate, today);

        // 최근 90일 동안 나와 MBTI가 같은 유저가 적은 메모의 빈도수 측정
        Mbti mbti = userInfoService.getUserMBTI();

        if(isNone(mbti)){
            return MBTIWordFrequencyResponse.builder()
                    .mbti(mbti)
                    .allWordFrequencies(
                            wordExtractionService.analyzeWords(
                                    memos.stream()
                                            .flatMap((m) -> Stream.of(
                                                    m.getContent(),
                                                    m.getEvent(),
                                                    m.getReason(),
                                                    m.getResult(),
                                                    m.getThought(),
                                                    m.getImprovements()))
                                            .toList())
                    )
                    .myWordFrequencies(List.of())
                    .build();
        }

        List<MemoDto> memoByMBTI = mbtiStatisticsRepository.getAllMemosByMBTIBetweenStartDateAndEndDate(mbti.toString(), startDate, today);

        return MBTIWordFrequencyResponse.builder()
                .mbti(mbti)
                .allWordFrequencies(
                        wordExtractionService.analyzeWords(
                                memos.stream()
                                        .flatMap((m) -> Stream.of(
                                                m.getContent(),
                                                m.getEvent(),
                                                m.getReason(),
                                                m.getResult(),
                                                m.getThought(),
                                                m.getImprovements()))
                                        .toList())
                )
                .myWordFrequencies(
                        wordExtractionService.analyzeWords(
                                memoByMBTI.stream()
                                        .flatMap((m) -> Stream.of(
                                                m.getContent(),
                                                m.getEvent(),
                                                m.getReason(),
                                                m.getResult(),
                                                m.getThought(),
                                                m.getImprovements()))
                                        .toList())
                )
                .build();
    }

    private static boolean isNone(Mbti mbti) {
        return mbti == null || mbti == Mbti.NONE;
    }

    public MBTISatisfactionAverageResponse getSatisfactionAveragesEachMBTILast90Days(LocalDate today, RegisterType registerType){
        LocalDate startDate = today.minusDays(PERIOD_CRITERIA);
        return MBTISatisfactionAverageResponse.builder()
                .mbti(userInfoService.getUserMBTI())
                .mbtiSatisfactionAverages(
                        mbtiStatisticsRepository
                                .getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(registerType, startDate, today))
                .build();
    }
}
