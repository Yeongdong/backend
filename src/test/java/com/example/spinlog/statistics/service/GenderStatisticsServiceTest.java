package com.example.spinlog.statistics.service;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.repository.GenderStatisticsRepository;
import com.example.spinlog.statistics.repository.dto.*;
import com.example.spinlog.statistics.service.dto.GenderDailyAmountSumResponse;
import com.example.spinlog.statistics.service.dto.GenderEmotionAmountAverageResponse;
import com.example.spinlog.statistics.service.dto.GenderWordFrequencyResponse;
import com.example.spinlog.statistics.service.dto.WordFrequency;
import com.example.spinlog.user.entity.Gender;
import org.assertj.core.groups.Tuple;
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
    class getAmountAveragesEachGenderAndEmotionLast90Days{
        @Test
        void LocalDate_파라미터를_받아서_90일_전_LocalDate와_해당_LocalDate를_레포지토리에게_전달한다() throws Exception {
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
        void RegisterType_파라미터를_그대로_레포지토리에게_전달한다(RegisterType registerType) throws Exception {
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
        void 레포지토리로부터_성별_감정별_금액_평균_데이터를_받아_성별로_grouping해서_반환한다() throws Exception {
            // given
            List<GenderEmotionAmountAverageDto> returned = List.of(
                    new GenderEmotionAmountAverageDto(Gender.MALE, Emotion.PROUD, 1L),
                    new GenderEmotionAmountAverageDto(Gender.MALE, Emotion.SAD, 2L),
                    new GenderEmotionAmountAverageDto(Gender.FEMALE, Emotion.PROUD, 3L),
                    new GenderEmotionAmountAverageDto(Gender.FEMALE, Emotion.SAD, 4L)
            );

            when(genderStatisticsRepository.getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(returned);

            // when
            List<GenderEmotionAmountAverageResponse> responses =
                    statisticsService.getAmountAveragesEachGenderAndEmotionLast90Days(LocalDate.now(), null);

            // then
            assertThat(responses)
                    .hasSize(2);

            List<Gender> genders = returned.stream()
                    .map(GenderEmotionAmountAverageDto::getGender)
                    .distinct()
                    .toList();
            assertThat(responses)
                    .extracting(GenderEmotionAmountAverageResponse::getGender)
                    .containsExactlyInAnyOrderElementsOf(genders);

            for(var response: responses){
                assertEmotionAmountAveragesGroupedByGender(response, returned);
            }
        }

        private static void assertEmotionAmountAveragesGroupedByGender(
                GenderEmotionAmountAverageResponse response,
                List<GenderEmotionAmountAverageDto> returned
        ) {
            List<Tuple> emotionAmountAveragesGroupedByGender = returned.stream()
                    .filter(a -> a.getGender()
                            .equals(response.getGender()))
                    .map(a -> new Tuple(a.getEmotion(),
                            a.getAmountAverage()))
                    .toList();
            assertThat(response.getEmotionAmountAverages())
                    .extracting("emotion", "amountAverage")
                    .containsExactlyInAnyOrderElementsOf(emotionAmountAveragesGroupedByGender);
        }
    }

    @Nested
    class getAmountSumsEachGenderAndDayLast90Days {
        @Test
        void LocalDate_파라미터를_받아서_90일_전_LocalDate와_해당_LocalDate를_레포지토리에게_전달한다() throws Exception {
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
        void RegisterType_파라미터를_그대로_레포지토리에게_전달한다(RegisterType registerType) throws Exception {
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
        void 레포지토리로부터_성별_일별_금액_총합_데이터를_받아_성별로_grouping해서_반환한다() throws Exception {
            // given
            List<GenderDailyAmountSumDto> returned = List.of(
                    new GenderDailyAmountSumDto(Gender.MALE, LocalDate.now(), 1L),
                    new GenderDailyAmountSumDto(Gender.MALE, LocalDate.now().minusDays(1L), 2L),
                    new GenderDailyAmountSumDto(Gender.FEMALE, LocalDate.now(), 3L),
                    new GenderDailyAmountSumDto(Gender.FEMALE, LocalDate.now().minusDays(1L), 4L)
            );
            when(genderStatisticsRepository.getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(returned);

            // when
            List<GenderDailyAmountSumResponse> responses =
                    statisticsService.getAmountSumsEachGenderAndDayLast90Days(LocalDate.now(), null);

            // then
            assertThat(responses)
                    .hasSize(2);

            List<Gender> genders = returned.stream()
                    .map(GenderDailyAmountSumDto::getGender)
                    .distinct()
                    .toList();
            assertThat(responses)
                    .extracting(GenderDailyAmountSumResponse::getGender)
                    .containsExactlyInAnyOrderElementsOf(genders);

            for(var response: responses){
                assertDailyAmountSumGroupedByGender(response, returned);
            }
        }

        private static void assertDailyAmountSumGroupedByGender(
                GenderDailyAmountSumResponse response,
                List<GenderDailyAmountSumDto> returned
        ) {
            List<Tuple> emotionAmountSumsGroupedByGender = returned.stream()
                    .filter(a -> a.getGender()
                            .equals(response.getGender()))
                    .map(a -> new Tuple(
                            a.getLocalDate(),
                            a.getAmountSum()))
                    .toList();
            assertThat(response.getDailyAmountSums())
                    .extracting("date", "amountSum")
                    .containsExactlyInAnyOrderElementsOf(emotionAmountSumsGroupedByGender);
        }
    }

    @Nested
    class getWordFrequenciesEachGenderLast90Days {
        @Test
        void LocalDate_파라미터를_받아서_90일_전_LocalDate와_해당_LocalDate를_레포지토리에게_전달한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getWordFrequenciesEachGenderLast90Days(now);

            // then
            verify(genderStatisticsRepository, times(2))
                    .getAllMemosByGenderBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }

        @Test
        void 레포지토리에게_MALE에_대한_메모_정보와_FEMALE에_대한_메모_정보를_요청한다() throws Exception {
            // when
            statisticsService.getWordFrequenciesEachGenderLast90Days(LocalDate.now());

            // then
            verify(genderStatisticsRepository)
                    .getAllMemosByGenderBetweenStartDateAndEndDate(
                            eq(Gender.MALE),
                            any(),
                            any());
            verify(genderStatisticsRepository)
                    .getAllMemosByGenderBetweenStartDateAndEndDate(
                            eq(Gender.FEMALE),
                            any(),
                            any());
        }

        @Test
        void 레포로부터_모든_메모_데이터를_받고_이를_평면화하여_WordExtractionService에게_보낸다() throws Exception {
            // given
            List<MemoDto> memos = List.of(
                    new MemoDto("c1", "e1", "t1", "r1", "r1", "i1"),
                    new MemoDto("c2", "e2", "t2", "r2", "r2", "i2"),
                    new MemoDto("c3", "e3", "t3", "r3", "r3", "i3"),
                    new MemoDto("c4", "e4", "t4", "r4", "r4", "i4")
            );
            when(genderStatisticsRepository.getAllMemosByGenderBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(memos)
                    .thenReturn(memos);

            // when
            statisticsService.getWordFrequenciesEachGenderLast90Days(LocalDate.now());

            // then
            List<String> flattedMemos = memos.stream()
                    .flatMap(m ->
                            Stream.of(
                                    m.getContent(),
                                    m.getThought(),
                                    m.getEvent(),
                                    m.getReason(),
                                    m.getResult(),
                                    m.getImprovements()))
                    .toList();
            verify(wordExtractionService, times(2))
                    .analyzeWords(argThat((argument) ->
                            areListsEqualIgnoringOrder(argument, flattedMemos)));
        }
        
        @Test
        void WordExtractionService로부터_성별로_메모에_대한_단어_빈도수_데이터를_받아서_하나의_객체로_반환한다() throws Exception {
            // givenq
            List<WordFrequency> returnedByWordExtractionService = List.of(
                    WordFrequency.builder()
                            .word("exampleWord")
                            .frequency(10L)
                            .build());
            when(wordExtractionService.analyzeWords(any()))
                    .thenReturn(returnedByWordExtractionService)
                    .thenReturn(returnedByWordExtractionService);
            
            // when
            GenderWordFrequencyResponse response = statisticsService.getWordFrequenciesEachGenderLast90Days(LocalDate.now());

            // then
            assertThat(response)
                    .extracting(GenderWordFrequencyResponse::getMaleWordFrequencies)
                    .isEqualTo(returnedByWordExtractionService);
            assertThat(response)
                    .extracting(GenderWordFrequencyResponse::getFemaleWordFrequencies)
                    .isEqualTo(returnedByWordExtractionService);
        }
    }

    @Nested
    class getSatisfactionAveragesEachGenderLast90Days {
        @Test
        void LocalDate_파라미터를_받아서_90일_전_LocalDate와_해당_LocalDate를_레포지토리에게_전달한다() throws Exception {
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

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void RegisterType_파라미터를_그대로_레포지토리에게_전달한다(RegisterType registerType) throws Exception {
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
        void 레포지토리로부터_성별_만족도_평균_데이터를_받아_그대로_반환한다() throws Exception {
            // given
            List<GenderSatisfactionAverageDto> returned = List.of(
                    GenderSatisfactionAverageDto.builder()
                            .gender(Gender.MALE)
                            .satisfactionAverage(1.0f)
                            .build());
            when(genderStatisticsRepository
                    .getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(returned);

            // when
            List<GenderSatisfactionAverageDto> response = statisticsService
                    .getSatisfactionAveragesEachGenderLast90Days(LocalDate.now(), null);

            // then
            assertThat(response).isEqualTo(returned);
        }
    }

    private boolean areListsEqualIgnoringOrder(List<String> l1, List<String> l2) {
        return l1.stream()
                .sorted()
                .toList()
                .equals(
                        l2.stream()
                                .sorted()
                                .toList());
    }
}