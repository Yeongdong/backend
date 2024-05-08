package com.example.spinlog.statistics.service;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.controller.dto.*;
import com.example.spinlog.statistics.repository.GenderStatisticsRepository;
import com.example.spinlog.statistics.repository.dto.*;
import com.example.spinlog.statistics.required_have_to_delete.UserInfoService;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GenderStatisticsServiceTest {
    @Mock
    WordExtractionService wordExtractionService;
    @Mock
    GenderStatisticsRepository genderStatisticsRepository;

    @InjectMocks
    GenderStatisticsService statisticsService;

    @Nested
    class 최근_90일_동안의_성별_감정별_금액_평균을_얻는_메서드{
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getAmountAveragesEachGenderAndEmotionLast90Days(now, null);

            // then
            verify(genderStatisticsRepository)
                    .getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getAmountAveragesEachGenderAndEmotionLast90Days(LocalDate.now(), registerType);

            // then
            verify(genderStatisticsRepository)
                    .getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(
                            eq(registerType),
                            any(),
                            any()
                    );
        }

        @Test
        void 성별과_감정에_대한_금액_평균을_성별로_그루핑해서_반환한다() throws Exception {
            // given
            List<GenderEmotionAmountAverageDto> inputs = List.of(
                    new GenderEmotionAmountAverageDto(Gender.MALE, "PROUD", 1L),
                    new GenderEmotionAmountAverageDto(Gender.MALE, "SAD", 2L),
                    new GenderEmotionAmountAverageDto(Gender.FEMALE, "PROUD", 3L),
                    new GenderEmotionAmountAverageDto(Gender.FEMALE, "SAD", 4L)
            );
            List<Boolean> visited = new ArrayList<>();
            for(int i=0;i<inputs.size();i++)
                visited.add(false);

            when(genderStatisticsRepository.getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(inputs);

            // when
            List<GenderEmotionAmountAverageResponse> responses =
                    statisticsService.getAmountAveragesEachGenderAndEmotionLast90Days(LocalDate.now(), null);

            // then
            assertThat(responses)
                    .hasSize(2);

            // TODO 그루핑하는 테스트 분리 & 리팩토링
            for(GenderEmotionAmountAverageResponse r: responses){
                for(int i=0;i<inputs.size();i++){
                    if(inputs.get(i).getGender().equals(r.getGender())){
                        final int index = i;
                        r.getEmotionAmountAverages()
                                .forEach(ea -> {
                                    if(ea.getEmotion().toString().equals(inputs.get(index).getEmotion())
                                            && ea.getAmountAverage().equals(inputs.get(index).getAmountAverage()))
                                        visited.set(index, true);
                                });
                    }
                }
            }

            assertThat(visited).doesNotContain(false);
        }
    }

    @Nested
    class 최근_90일_동안의_성별_일별_금액_총합을_얻는_메서드 {
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getAmountSumsEachGenderAndDayLast90Days(now, null);

            // then
            verify(genderStatisticsRepository)
                    .getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getAmountSumsEachGenderAndDayLast90Days(LocalDate.now(), registerType);

            // then
            verify(genderStatisticsRepository)
                    .getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(
                            eq(registerType),
                            any(),
                            any()
                    );
        }

        @Test
        void 성별_일별_금액_평균을_성별로_그루핑해서_반환한다() throws Exception {
            // given
            List<GenderDailyAmountSumDto> inputs = List.of(
                    new GenderDailyAmountSumDto(Gender.MALE, LocalDate.now(), 1L),
                    new GenderDailyAmountSumDto(Gender.MALE, LocalDate.now().minusDays(1L), 2L),
                    new GenderDailyAmountSumDto(Gender.FEMALE, LocalDate.now(), 3L),
                    new GenderDailyAmountSumDto(Gender.FEMALE, LocalDate.now().minusDays(1L), 4L)
            );
            List<Boolean> visited = new ArrayList<>();
            for(int i=0;i<inputs.size();i++)
                visited.add(false);

            when(genderStatisticsRepository.getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(inputs);

            // when
            List<GenderDailyAmountSumResponse> responses =
                    statisticsService.getAmountSumsEachGenderAndDayLast90Days(LocalDate.now(), null);

            // then
            assertThat(responses)
                    .hasSize(2);

            for(GenderDailyAmountSumResponse r: responses){
                for(int i=0;i<inputs.size();i++){
                    if(inputs.get(i).getGender().equals(r.getGender())){
                        final int index = i;
                        r.getEmotionAmountSums()
                                .forEach(ea -> {
                                    if(ea.getDate().equals(inputs.get(index).getLocalDate())
                                            && ea.getAmountSum().equals(inputs.get(index).getAmountSum()))
                                        visited.set(index, true);
                                });
                    }
                }
            }

            assertThat(visited).doesNotContain(false);
        }
    }

    @Nested
    class 최근_90일_동안의_성별_메모_빈도수를_얻는_메서드 {
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getWordFrequenciesLast90Days(now);

            // then
            verify(genderStatisticsRepository)
                    .getAllMemosByGenderBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }

        @Test
        void 레포에게_성별로_MALE과_FEMALE을_전달한다() throws Exception {
            // when
            statisticsService.getWordFrequenciesLast90Days(LocalDate.now());

            // then
            verify(genderStatisticsRepository, times(1))
                    .getAllMemosByGenderBetweenStartDateAndEndDate(
                            eq(Gender.MALE),
                            any(),
                            any());
            verify(genderStatisticsRepository, times(1))
                    .getAllMemosByGenderBetweenStartDateAndEndDate(
                            eq(Gender.FEMALE),
                            any(),
                            any());
        }

        @Test
        void 레포로부터_리스트를_받고_이를_평면화하여_WordExtractionService에게_보낸다() throws Exception {
            // given
            List<MemoDto> memos = List.of(
                    new MemoDto("c1", "e1", "t1", "r1", "r1", "i1"),
                    new MemoDto("c2", "e2", "t2", "r2", "r2", "i2"),
                    new MemoDto("c3", "e3", "t3", "r3", "r3", "i3"),
                    new MemoDto("c4", "e4", "t4", "r4", "r4", "i4")
            );
            when(genderStatisticsRepository.getAllMemosByGenderBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(memos)
                    .thenReturn(null);

            // when
            statisticsService.getWordFrequenciesLast90Days(LocalDate.now());

            // then
            ArgumentCaptor<List<String>> captorWords = ArgumentCaptor.forClass(List.class);
            verify(wordExtractionService, times(2))
                    .analyzeWords(captorWords.capture());

            List<String> value = captorWords.getAllValues().get(0);
            List<String> list = memos.stream()
                    .flatMap(m ->
                            Stream.of(
                                    m.getContent(),
                                    m.getThought(),
                                    m.getEvent(),
                                    m.getReason(),
                                    m.getResult(),
                                    m.getImprovements()))
                    .toList();

            assertThat(list).containsExactlyInAnyOrderElementsOf(value);
        }
    }

    @Nested
    class 최근_90일_동안의_성별_만족도_평균을_얻는_메서드 {
        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getSatisfactionAveragesEachGenderLast90Days(LocalDate.now(), registerType);

            // then
            verify(genderStatisticsRepository)
                    .getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                            eq(registerType),
                            any(),
                            any()
                    );
        }

        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getSatisfactionAveragesEachGenderLast90Days(now, null);

            // then
            verify(genderStatisticsRepository)
                    .getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }
    }
}