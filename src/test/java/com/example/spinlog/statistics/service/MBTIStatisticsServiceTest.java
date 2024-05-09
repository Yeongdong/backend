package com.example.spinlog.statistics.service;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.service.dto.MBTIDailyAmountSumResponse;
import com.example.spinlog.statistics.service.dto.MBTIEmotionAmountAverageResponse;
import com.example.spinlog.statistics.service.dto.MBTIWordFrequencyResponse;
import com.example.spinlog.statistics.repository.MBTIStatisticsRepository;
import com.example.spinlog.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.spinlog.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import com.example.spinlog.statistics.repository.dto.MemoDto;
import com.example.spinlog.statistics.required_have_to_delete.UserInfoService;
import com.example.spinlog.user.entity.Mbti;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
class MBTIStatisticsServiceTest {
    @Mock
    WordExtractionService wordExtractionService;
    @Mock
    MBTIStatisticsRepository mbtiStatisticsRepository;
    @Mock
    UserInfoService userInfoService;

    @InjectMocks
    MBTIStatisticsService statisticsService;

    @Nested
    class 최근_90일_동안의_MBTI별_감정별_금액_평균을_얻는_메서드{
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(now, null);

            // then
            verify(mbtiStatisticsRepository)
                    .getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(LocalDate.now(), registerType);

            // then
            verify(mbtiStatisticsRepository)
                    .getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                            eq(registerType),
                            any(),
                            any()
                    );
        }

        @Test
        void MBTI와_감정에_대한_금액_평균을_MBTI_별로_그루핑해서_반환한다() throws Exception {
            // given
            List<MBTIEmotionAmountAverageDto> inputs = List.of(
                    new MBTIEmotionAmountAverageDto("I", "PROUD", 1L),
                    new MBTIEmotionAmountAverageDto("I", "SAD", 2L),
                    new MBTIEmotionAmountAverageDto("E", "PROUD", 3L),
                    new MBTIEmotionAmountAverageDto("E", "SAD", 4L)
            );
            List<Boolean> visited = new ArrayList<>();
            for(int i=0;i<inputs.size();i++)
                visited.add(false);

            when(mbtiStatisticsRepository.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(inputs);

            // when
            List<MBTIEmotionAmountAverageResponse.MBTIEmotionAmountAverage> responses =
                    statisticsService.getAmountAveragesEachMBTIAndEmotionLast90Days(LocalDate.now(), null)
                            .getMbtiEmotionAmountAverages();

            // then
            assertThat(responses)
                    .hasSize(2);

            // TODO 그루핑하는 테스트 분리 & 리팩토링
            for(MBTIEmotionAmountAverageResponse.MBTIEmotionAmountAverage r: responses){
                for(int i=0;i<inputs.size();i++){
                    if(inputs.get(i).getMbtiFactor().equals(r.getMbtiFactor().toString())){
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
    class 최근_90일_동안의_MBTI별_일별_금액_총합을_얻는_메서드 {
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();

            // when
            statisticsService.getAmountSumsEachMBTIAndDayLast90Days(now, null);

            // then
            verify(mbtiStatisticsRepository)
                    .getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getAmountSumsEachMBTIAndDayLast90Days(LocalDate.now(), registerType);

            // then
            verify(mbtiStatisticsRepository)
                    .getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                            eq(registerType),
                            any(),
                            any()
                    );
        }

        @Test
        void MBTI와_일별_금액_평균을_MBTI_별로_그루핑해서_반환한다() throws Exception {
            // given
            List<MBTIDailyAmountSumDto> inputs = List.of(
                    new MBTIDailyAmountSumDto("I", LocalDate.now(), 1L),
                    new MBTIDailyAmountSumDto("I", LocalDate.now().minusDays(1L), 2L),
                    new MBTIDailyAmountSumDto("E", LocalDate.now(), 3L),
                    new MBTIDailyAmountSumDto("E", LocalDate.now().minusDays(1L), 4L)
            );
            List<Boolean> visited = new ArrayList<>();
            for(int i=0;i<inputs.size();i++)
                visited.add(false);

            when(mbtiStatisticsRepository.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(inputs);

            // when
            List<MBTIDailyAmountSumResponse.MBTIDailyAmountSum> responses =
                    statisticsService.getAmountSumsEachMBTIAndDayLast90Days(LocalDate.now(), null)
                            .getMbtiDailyAmountSums();

            // then
            assertThat(responses)
                    .hasSize(2);

            for(MBTIDailyAmountSumResponse.MBTIDailyAmountSum r: responses){
                for(int i=0;i<inputs.size();i++){
                    if(inputs.get(i).getMbtiFactor().equals(r.getMbtiFactor().toString())){
                        final int index = i;
                        r.getDailyAmountSums()
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
    class 최근_90일_동안의_메모_빈도수를_얻는_메서드 {
        @Test
        void 현재_날짜를_레포에게_전달하면_DB에게_최근_90일의_데이터만_요청한다() throws Exception {
            // given
            LocalDate now = LocalDate.now();
            when(userInfoService.getUserMBTI())
                    .thenReturn(Mbti.NONE);

            // when
            statisticsService.getWordFrequenciesLast90Days(now);

            // then
            verify(mbtiStatisticsRepository)
                    .getAllMemosByMBTIBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }

        // TODO 레포라는 이름 괜찮나?
        @Test
        void 레포에게_MBTI_파라미터로_로그인_한_유저의_MBTI와_NONE을_전달한다() throws Exception {
            // given
            Mbti userMBTI = Mbti.ISTJ;
            when(userInfoService.getUserMBTI())
                    .thenReturn(userMBTI);

            // when
            statisticsService.getWordFrequenciesLast90Days(LocalDate.now());
            
            // then
            verify(mbtiStatisticsRepository, times(1))
                    .getAllMemosByMBTIBetweenStartDateAndEndDate(
                            eq(Mbti.NONE.toString()),
                            any(),
                            any());
            verify(mbtiStatisticsRepository, times(1))
                    .getAllMemosByMBTIBetweenStartDateAndEndDate(
                            eq(userMBTI.toString()),
                            any(),
                            any());
        }

        @Test
        void UserService로부터_받는_MBTI가_유효하지_않은_값이라면_전체_메모만_반환한다() throws Exception {
            // given
            when(userInfoService.getUserMBTI())
                    .thenReturn(Mbti.NONE);

            // when
            MBTIWordFrequencyResponse response = statisticsService.getWordFrequenciesLast90Days(LocalDate.now());

            // then
            assertThat(response.getMyWordFrequencies())
                    .hasSize(0);
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
            when(mbtiStatisticsRepository.getAllMemosByMBTIBetweenStartDateAndEndDate(any(), any(), any()))
                    .thenReturn(memos);
            when(userInfoService.getUserMBTI())
                    .thenReturn(Mbti.NONE);

            // when
            statisticsService.getWordFrequenciesLast90Days(LocalDate.now());

            // then
            ArgumentCaptor<List<String>> captorWords = ArgumentCaptor.forClass(List.class);

            verify(wordExtractionService)
                    .analyzeWords(captorWords.capture());

            List<String> value = captorWords.getValue();
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
    class 최근_90일_동안의_MBTI별_만족도_평균을_얻는_메서드 {

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void 입력한_RegisterType을_레포에게_그대로_입력한다(RegisterType registerType) throws Exception {
            // when
            statisticsService.getSatisfactionAveragesEachMBTILast90Days(LocalDate.now(), registerType);

            // then
            verify(mbtiStatisticsRepository)
                    .getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
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
            statisticsService.getSatisfactionAveragesEachMBTILast90Days(now, null);

            // then
            verify(mbtiStatisticsRepository)
                    .getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                            any(),
                            eq(now.minusDays(90)),
                            eq(now));
        }
    }
}