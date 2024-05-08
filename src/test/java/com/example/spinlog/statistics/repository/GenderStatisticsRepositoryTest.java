package com.example.spinlog.statistics.repository;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.global.entity.BaseTimeEntity;
import com.example.spinlog.statistics.repository.dto.GenderDailyAmountSumDto;
import com.example.spinlog.statistics.repository.dto.GenderEmotionAmountAverageDto;
import com.example.spinlog.statistics.repository.dto.GenderSatisfactionAverageDto;
import com.example.spinlog.statistics.repository.dto.MemoDto;
import com.example.spinlog.statistics.required_have_to_delete.UserRepository;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GenderStatisticsRepositoryTest {
    @Autowired
    GenderStatisticsRepository genderStatisticsRepository;

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em; // flush 하기 위해 필요 (JPA 쓰기 지연 방지)

    @Nested
    class 성별_감정별_금액_평균을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType registerType = RegisterType.SPEND;
            Emotion removedEmotion = Emotion.SAD;
            Emotion survivedEmotion = Emotion.PROUD;
            List<Article> removedArticles = List.of(
                    makeArticle(user.getId(), registerType, removedEmotion, period.startDate.atStartOfDay().minusSeconds(1L), 1000),
                    makeArticle(user.getId(), registerType, removedEmotion, period.endDate.atStartOfDay().plusSeconds(1L), 1000)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, survivedEmotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), registerType, survivedEmotion, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderEmotionAmountAverageDto> dtos = genderStatisticsRepository.getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderEmotionAmountAverageDto::getEmotion)
                    .containsOnly(survivedEmotion.toString())
                    .isNotIn(removedEmotion.toString());
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType removedRegisterType = RegisterType.SAVE;
            RegisterType registerType = RegisterType.SPEND;
            Emotion removedEmotion = Emotion.SAD;
            Emotion survivedEmotion = Emotion.PROUD;
            List<Article> removedArticles = List.of(
                    makeArticle(user.getId(), removedRegisterType, removedEmotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), removedRegisterType, removedEmotion, period.endDate.atStartOfDay(), 1000)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, survivedEmotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), registerType, survivedEmotion, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderEmotionAmountAverageDto> dtos = genderStatisticsRepository.getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderEmotionAmountAverageDto::getEmotion)
                    .containsOnly(survivedEmotion.toString())
                    .isNotIn(removedEmotion.toString());
        }

        @Test
        @Order(1)
        void 성별_감정별_금액의_평균을_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType registerType = RegisterType.SPEND;
            Emotion emotion = Emotion.PROUD;
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, emotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), registerType, emotion, period.endDate.atStartOfDay(), 2000),
                    makeArticle(user.getId(), registerType, emotion, period.endDate.atStartOfDay(), 3000)
            );
            Long amountAverage = (long) articles.stream()
                    .map(Article::getAmount)
                    .reduce(Integer::sum)
                    .get() / articles.size();

            em.flush();

            // when
            List<GenderEmotionAmountAverageDto> dtos = genderStatisticsRepository.getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .hasSize(1)
                    .extracting(GenderEmotionAmountAverageDto::getGender)
                    .containsOnly(Gender.MALE);
            assertThat(dtos)
                    .extracting(GenderEmotionAmountAverageDto::getEmotion)
                    .containsOnly(emotion.toString());
            assertThat(dtos)
                    .extracting(GenderEmotionAmountAverageDto::getAmountAverage)
                    .containsOnly(amountAverage);
        }

        @Test
        @Order(2)
        void 금액의_평균을_반활할_때_100의_자리수에서_반올림된다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType registerType = RegisterType.SPEND;
            Emotion emotion = Emotion.PROUD;
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, emotion, period.startDate.atStartOfDay(), 1100),
                    makeArticle(user.getId(), registerType, emotion, period.endDate.atStartOfDay(), 2200),
                    makeArticle(user.getId(), registerType, emotion, period.endDate.atStartOfDay(), 3300)
            );
            Long amountAverage = (long) articles.stream()
                    .map(Article::getAmount)
                    .reduce(Integer::sum)
                    .get() / articles.size();
            amountAverage = roundingAverage(amountAverage);

            em.flush();

            // when
            List<GenderEmotionAmountAverageDto> dtos = genderStatisticsRepository.getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderEmotionAmountAverageDto::getAmountAverage)
                    .containsOnly(amountAverage);
        }
    }

    @Nested
    class 성별_일별_금액_합을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType registerType = RegisterType.SPEND;
            List<Article> removedArticles = List.of(
                    makeArticle(user.getId(), registerType, null, period.startDate.atStartOfDay().minusSeconds(1L), 1000),
                    makeArticle(user.getId(), registerType, null, period.endDate.atStartOfDay().plusSeconds(1L), 1000)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, null, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), registerType, null, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderDailyAmountSumDto> dtos = genderStatisticsRepository.getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderDailyAmountSumDto::getLocalDate)
                    .containsAll(
                            articles.stream()
                                    .map(a -> a.getCreatedDate().toLocalDate())
                                    .toList()
                    )
                    .doesNotContain(
                            period.startDate.minusDays(1),
                            period.endDate.plusDays(1));
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType removedRegisterType = RegisterType.SAVE;
            RegisterType registerType = RegisterType.SPEND;
            List<Article> removedArticles = List.of(
                    makeArticle(user.getId(), removedRegisterType, null, period.startDate.atStartOfDay().minusSeconds(1L), 1000),
                    makeArticle(user.getId(), removedRegisterType, null, period.endDate.atStartOfDay().plusSeconds(1L), 1000)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, null, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), registerType, null, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderDailyAmountSumDto> dtos = genderStatisticsRepository.getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            // TODO 날짜를 기준으로 객체를 찾고 싶다
            assertThat(dtos)
                    .extracting(GenderDailyAmountSumDto::getLocalDate)
                    .containsAll(
                            articles.stream()
                                    .map(a -> a.getCreatedDate().toLocalDate())
                                    .toList()
                    )
                    .doesNotContain(
                            period.startDate.minusDays(1),
                            period.endDate.plusDays(1));
        }

        @Test
        void 성별_일별_금액의_합을_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType registerType = RegisterType.SPEND;
            Emotion emotion = Emotion.PROUD;
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, emotion, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), registerType, emotion, period.startDate.atStartOfDay(), 2000),
                    makeArticle(user.getId(), registerType, emotion, period.startDate.atStartOfDay(), 3000)
            );
            Long sum = articles.stream()
                    .mapToLong(a -> a.getAmount().longValue())
                    .reduce(Long::sum).orElseGet(() -> -1L);
            em.flush();

            // when
            List<GenderDailyAmountSumDto> dtos = genderStatisticsRepository.getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .hasSize(1)
                    .extracting(GenderDailyAmountSumDto::getGender)
                    .containsOnly(Gender.MALE);
            assertThat(dtos)
                    .extracting(GenderDailyAmountSumDto::getLocalDate)
                    .containsOnly(period.startDate);
            assertThat(dtos)
                    .extracting(GenderDailyAmountSumDto::getAmountSum)
                    .containsOnly(sum);
        }
    }
    @Nested
    class 메모를_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));

            String things = "things";
            String removedThings = "removed";
            List<Article> removedArticles = List.of(
                    makeArticle(user.getId(), RegisterType.SPEND, null, period.startDate.atStartOfDay().minusSeconds(1L), removedThings),
                    makeArticle(user.getId(), RegisterType.SPEND, null, period.endDate.atStartOfDay().plusSeconds(1L), removedThings)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), RegisterType.SPEND, null, period.startDate.atStartOfDay(), things),
                    makeArticle(user.getId(), RegisterType.SPEND, null, period.endDate.atStartOfDay(), things)
            );
            em.flush();

            // when
            List<MemoDto> dtos = genderStatisticsRepository.getAllMemosByGenderBetweenStartDateAndEndDate(
                    Gender.MALE,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MemoDto::getContent)
                    .containsOnly(things)
                    .doesNotContain(removedThings);
        }

        @Test
        void 입력한_성별을_가지고_있는_유저들의_메모만_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .gender(Gender.MALE)
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .email("gks@tkd")
                            .name("Han")
                            .gender(Gender.FEMALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));

            String things = "things";
            String removedThings = "removed";
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser.getId(), RegisterType.SPEND, null, period.startDate.atStartOfDay(), removedThings),
                    makeArticle(otherUser.getId(), RegisterType.SPEND, null, period.endDate.atStartOfDay(), removedThings)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), RegisterType.SPEND, null, period.startDate.atStartOfDay(), things),
                    makeArticle(user.getId(), RegisterType.SPEND, null, period.endDate.atStartOfDay(), things)
            );
            em.flush();

            // when
            List<MemoDto> dtos = genderStatisticsRepository.getAllMemosByGenderBetweenStartDateAndEndDate(
                    Gender.MALE,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MemoDto::getContent)
                    .containsOnly(things)
                    .doesNotContain(removedThings);
        }
    }
    @Nested
    class 성별_만족도_평균을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .email("gks@tkd")
                            .name("other")
                            .mbti(Mbti.ENFP)
                            .gender(Gender.FEMALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType registerType = RegisterType.SPEND;
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser.getId(), registerType, null, period.startDate.atStartOfDay().minusSeconds(1L), 1.0f),
                    makeArticle(otherUser.getId(), registerType, null, period.endDate.atStartOfDay().plusSeconds(1L), 1.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, null, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), registerType, null, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderSatisfactionAverageDto> dtos = genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderSatisfactionAverageDto::getGender)
                    .contains(user.getGender())
                    .doesNotContain(otherUser.getGender());
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .email("gks@tkd")
                            .name("other")
                            .mbti(Mbti.ENFP)
                            .gender(Gender.FEMALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType removedRegisterType = RegisterType.SAVE;
            RegisterType registerType = RegisterType.SPEND;
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser.getId(), removedRegisterType, null, period.startDate.atStartOfDay(), 1.0f),
                    makeArticle(otherUser.getId(), removedRegisterType, null, period.endDate.atStartOfDay(), 1.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, null, period.startDate.atStartOfDay(), 1000),
                    makeArticle(user.getId(), registerType, null, period.endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderSatisfactionAverageDto> dtos = genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderSatisfactionAverageDto::getGender)
                    .contains(user.getGender())
                    .doesNotContain(otherUser.getGender());
        }

        @Test
        @Order(1)
        void 성별_만족도의_평균을_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .email("gks@tkd")
                            .name("other")
                            .mbti(Mbti.ENFP)
                            .gender(Gender.FEMALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType registerType = RegisterType.SPEND;
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser.getId(), registerType, null, period.startDate.atStartOfDay(), 1.0f),
                    makeArticle(otherUser.getId(), registerType, null, period.endDate.atStartOfDay(), 2.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, null, period.startDate.atStartOfDay(), 3.0f),
                    makeArticle(user.getId(), registerType, null, period.endDate.atStartOfDay(), 4.0f)
            );
            em.flush();

            // when
            List<GenderSatisfactionAverageDto> dtos = genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(
                    dtos.stream()
                            .filter(d -> {
                                Gender gender = d.getGender();
                                if(user.getGender().equals(gender))
                                    return true;
                                return false;
                            })
                            .map(GenderSatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(3.5f);
            assertThat(
                    dtos.stream()
                            .filter(d -> {
                                Gender gender = d.getGender();
                                if(otherUser.getGender().equals(gender))
                                    return true;
                                return false;
                            })
                            .map(GenderSatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(1.5f);
        }

        @Test
        @Order(2)
        void 만족도의_평균을_소수_둘째_자리에서_반올림해서_반환한다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("gks@gks")
                            .name("Han")
                            .mbti(Mbti.ISTJ)
                            .gender(Gender.MALE)
                            .build()
            );
            User otherUser = userRepository.save(
                    User.builder()
                            .email("gks@tkd")
                            .name("other")
                            .mbti(Mbti.ENFP)
                            .gender(Gender.FEMALE)
                            .build()
            );

            DatePeriod period = new DatePeriod(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1));
            RegisterType registerType = RegisterType.SPEND;
            List<Article> removedArticles = List.of(
                    makeArticle(otherUser.getId(), registerType, null, period.startDate.atStartOfDay(), 1.04f),
                    makeArticle(otherUser.getId(), registerType, null, period.endDate.atStartOfDay(), 2.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(user.getId(), registerType, null, period.startDate.atStartOfDay(), 3.04f),
                    makeArticle(user.getId(), registerType, null, period.endDate.atStartOfDay(), 4.0f)
            );
            em.flush();

            // when
            List<GenderSatisfactionAverageDto> dtos = genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                    registerType,
                    period.startDate,
                    period.endDate
            );

            // then
            assertThat(
                    dtos.stream()
                            .filter(d -> {
                                Gender gender = d.getGender();
                                if(user.getGender().equals(gender))
                                    return true;
                                return false;
                            })
                            .map(GenderSatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(3.5f);
            assertThat(
                    dtos.stream()
                            .filter(d -> {
                                Gender gender = d.getGender();
                                if(otherUser.getGender().equals(gender))
                                    return true;
                                return false;
                            })
                            .map(GenderSatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(1.5f);
        }
    }
    Article makeArticle(Long userId, RegisterType registerType, Emotion emotion, LocalDateTime dateTime, int amount) {
        Article article = Article.builder()
                .registerType(registerType)
                .emotion(emotion)
                .amount(amount)
                .userId(userId)
                .build();
        articleRepository.save(article);

        try {
            Field createdDate = BaseTimeEntity.class.getDeclaredField("createdDate");
            createdDate.setAccessible(true);
            createdDate.set(article, dateTime);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return article;
    }
    Article makeArticle(Long userId, RegisterType registerType, Emotion emotion, LocalDateTime dateTime, float satisfaction) {
        Article article = Article.builder()
                .registerType(registerType)
                .emotion(emotion)
                .satisfaction(satisfaction)
                .userId(userId)
                .build();
        articleRepository.save(article);

        try {
            Field createdDate = BaseTimeEntity.class.getDeclaredField("createdDate");
            createdDate.setAccessible(true);
            createdDate.set(article, dateTime);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return article;
    }

    Article makeArticle(Long userId, RegisterType registerType, Emotion emotion, LocalDateTime dateTime, String content) {
        Article article = Article.builder()
                .registerType(registerType)
                .emotion(emotion)
                .content(content)
                .userId(userId)
                .build();
        articleRepository.save(article);

        try {
            Field createdDate = BaseTimeEntity.class.getDeclaredField("createdDate");
            createdDate.setAccessible(true);
            createdDate.set(article, dateTime);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return article;
    }

    private static Long roundingAverage(Long amountAverage) {
        amountAverage += 500L;
        amountAverage = amountAverage - (amountAverage % 1000);
        return amountAverage;
    }
    @Getter
    @AllArgsConstructor
    static class DatePeriod {

        private LocalDate startDate;
        private LocalDate endDate;
    }
}