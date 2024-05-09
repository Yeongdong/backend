package com.example.spinlog.statistics.controller;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.statistics.service.GenderStatisticsService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(GenderStatisticsController.class)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 때문에 추가함 TODO (이 어노테이션을 configuration으로 분리하면 없앨 수 있음)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GenderStatisticsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    GenderStatisticsService genderStatisticsService;

    @Nested
    class 성별_감정별_금액_평균_통계_API {
        @Test
        @DisplayName("/api/statistics/gender/emotion/amounts/average 로 요청하면 getAmountAveragesEachGenderAndEmotionLast90Days 메서드가 실행 된다.")
        void 아래_path로_요청하면_genderStatisticsService의_getAmountAverages_메서드가_실행된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/emotion/amounts/average"));

            // then
            verify(genderStatisticsService, times(1))
                    .getAmountAveragesEachGenderAndEmotionLast90Days(any(), any());
        }

        @Test
        void registerType_쿼리_파라미터를_입력하지_않으면_defaultValue로_SPEND가_입력된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/emotion/amounts/average"));

            // then
            verify(genderStatisticsService)
                    .getAmountAveragesEachGenderAndEmotionLast90Days(any(), eq(RegisterType.SPEND));
        }

        @Test
        void 올바르지_않은_registerType_쿼리_파라미터를_입력하면_400을_반환한다() throws Exception {
            // given
            String invalidRegisterType = "Invalid";

            // when // then
            mockMvc.perform(
                    get("/api/statistics/gender/emotion/amounts/average?registerType=" + invalidRegisterType))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void registerType_쿼리_파라미터로_SPEND나_SAVE를_입력해야_한다(String registerType) throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/emotion/amounts/average?registerType=" + registerType));

            // then
            verify(genderStatisticsService, times(1))
                    .getAmountAveragesEachGenderAndEmotionLast90Days(any(), eq(RegisterType.valueOf(registerType)));
        }
    }

    @Nested
    class 성별_일별_금액_총합_통계_API {
        @Test
        @DisplayName("/api/statistics/gender/daily/amounts/sum 로 요청하면 getAmountSumsEachGenderAndDayLast90Days 메서드가 실행 된다.")
        void 아래_path로_요청하면_genderStatisticsService의_getAmountSums_메서드가_실행된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/daily/amounts/sum"));

            // then
            verify(genderStatisticsService, times(1))
                    .getAmountSumsEachGenderAndDayLast90Days(any(), any());
        }

        @Test
        void registerType_쿼리_파라미터를_입력하지_않으면_defaultValue로_SPEND가_입력된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/daily/amounts/sum"));

            // then
            verify(genderStatisticsService)
                    .getAmountSumsEachGenderAndDayLast90Days(any(), eq(RegisterType.SPEND));
        }

        @Test
        void 올바르지_않은_registerType_쿼리_파라미터를_입력하면_400을_반환한다() throws Exception {
            // given
            String invalidRegisterType = "Invalid";

            // when // then
            mockMvc.perform(
                    get("/api/statistics/gender/daily/amounts/sum?registerType=" + invalidRegisterType))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void registerType_쿼리_파라미터로_SPEND나_SAVE를_입력해야_한다(String registerType) throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/daily/amounts/sum?registerType=" + registerType));

            // then
            verify(genderStatisticsService, times(1))
                    .getAmountSumsEachGenderAndDayLast90Days(any(), eq(RegisterType.valueOf(registerType)));
        }
    }

    @Nested
    class 성별_단어_빈도수_통계_API {
        @Test
        @DisplayName("/api/statistics/gender/word/frequencies 로 요청하면 getWordFrequenciesEachGenderLast90Days 메서드가 실행 된다.")
        void 아래_path로_요청하면_genderStatisticsService의_getWordFrequencies_메서드가_실행된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/word/frequencies"));

            // then
            verify(genderStatisticsService, times(1))
                    .getWordFrequenciesEachGenderLast90Days(any());
        }
    }

    @Nested
    class 성별_만족도_평균_통계_API {
        @Test
        @DisplayName("/api/statistics/gender/statisfactions/average 로 요청하면 getSatisfactionAveragesEachGenderLast90Days 메서드가 실행 된다.")
        void 아래_path로_요청하면_genderStatisticsService의_getSatisfactionAverages_메서드가_실행된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/statisfactions/average"));

            // then
            verify(genderStatisticsService, times(1))
                    .getSatisfactionAveragesEachGenderLast90Days(any(), any());
        }

        @Test
        void registerType_쿼리_파라미터를_입력하지_않으면_defaultValue로_SPEND가_입력된다() throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/statisfactions/average"));

            // then
            verify(genderStatisticsService)
                    .getSatisfactionAveragesEachGenderLast90Days(any(), eq(RegisterType.SPEND));
        }

        @Test
        void 올바르지_않은_registerType_쿼리_파라미터를_입력하면_400을_반환한다() throws Exception {
            // given
            String invalidRegisterType = "Invalid";

            // when // then
            mockMvc.perform(get("/api/statistics/gender/statisfactions/average?registerType=" + invalidRegisterType))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"SPEND", "SAVE"})
        void registerType_쿼리_파라미터로_SPEND나_SAVE를_입력해야_한다(String registerType) throws Exception {
            // when
            mockMvc.perform(get("/api/statistics/gender/statisfactions/average?registerType=" + registerType));

            // then
            verify(genderStatisticsService, times(1))
                    .getSatisfactionAveragesEachGenderLast90Days(any(), eq(RegisterType.valueOf(registerType)));
        }
    }
}