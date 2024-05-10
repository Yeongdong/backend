package com.example.spinlog.statistics.repository;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.global.entity.BaseTimeEntity;
import com.example.spinlog.statistics.entity.MBTIFactor;
import com.example.spinlog.statistics.repository.dto.*;
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

    /**
     * DB가 데이터를 필터링 해서 반환하는데, 필터링 됐음을 유저의 Gender로 구분한다.
     * MALE -> 필터링 되지 않고 그대로 받환되는 데이터의 Gender
     * FEMALE -> 필터링 되어 받을 수 없는 데이터의 Gender
     * */
    User survivedUser;
    Gender survivedGender = Gender.MALE;
    User filteredUser;
    Gender filteredGender = Gender.FEMALE;

    LocalDate startDate, endDate;

    /**
     * 필터링 결과를 구분하기 위해, 필터링 대상 MBTI를 가지고 있는 유저 객체 2개를 생성한다.
     * */
    @BeforeEach
    public void createTwoUserToDivideFilteringResultAndLocalDateRange() {
        survivedUser = User.builder()
                .email("survived@email")
                .name("survivedUser")
                .mbti(Mbti.ISTJ)
                .gender(survivedGender)
                .build();
        filteredUser = User.builder()
                .email("filtered@email")
                .name("filteredUser")
                .mbti(Mbti.ENFP)
                .gender(filteredGender) // 정반대의 Gender 입력 -> 이 gender는 필터링 되는 gender
                .build();
        userRepository.save(survivedUser);
        userRepository.save(filteredUser);

        startDate = LocalDate.now().minusDays(2);
        endDate = LocalDate.now().minusDays(1);
    }

    @Nested
    class 성별_감정별_금액_평균을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            RegisterType registerType = RegisterType.SPEND;
            Emotion emotion = Emotion.PROUD;
            List<Article> removedArticles = List.of(
                    makeArticle(filteredUser.getId(), registerType, emotion, startDate.atStartOfDay().minusSeconds(1L), 1000),
                    makeArticle(filteredUser.getId(), registerType, emotion, endDate.atStartOfDay().plusSeconds(1L), 1000)
            );
            List<Article> survivedArticles = List.of(
                    makeArticle(survivedUser.getId(), registerType, emotion, startDate.atStartOfDay(), 1000),
                    makeArticle(survivedUser.getId(), registerType, emotion, endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderEmotionAmountAverageDto> dtos = genderStatisticsRepository.getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(GenderEmotionAmountAverageDto::getGender)
                    .distinct()
                    .toList())
                    .containsOnly(survivedGender)
                    .doesNotContain(filteredGender);
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            RegisterType survivedRegisterType = RegisterType.SPEND;
            RegisterType filteredRegisterType = RegisterType.SAVE;
            Emotion emotion = Emotion.PROUD;
            List<Article> removedArticles = List.of(
                    makeArticle(filteredUser.getId(), filteredRegisterType, emotion, startDate.atStartOfDay(), 1000),
                    makeArticle(filteredUser.getId(), filteredRegisterType, emotion, endDate.atStartOfDay(), 1000)
            );
            List<Article> survivedArticles = List.of(
                    makeArticle(survivedUser.getId(), survivedRegisterType, emotion, startDate.atStartOfDay(), 1000),
                    makeArticle(survivedUser.getId(), survivedRegisterType, emotion, endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderEmotionAmountAverageDto> dtos = genderStatisticsRepository.getAmountAveragesEachGenderAndEmotionBetweenStartDateAndEndDate(
                    survivedRegisterType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(GenderEmotionAmountAverageDto::getGender)
                    .distinct()
                    .toList())
                    .containsOnly(survivedGender)
                    .doesNotContain(filteredGender);
        }

        @Test
        @Order(1)
        void 성별_감정별_금액의_평균을_반환한다() throws Exception {
            // given
            RegisterType registerType = RegisterType.SPEND;
            Emotion emotion = Emotion.PROUD;
            List<Article> articles = List.of(
                    makeArticle(survivedUser.getId(), registerType, emotion, startDate.atStartOfDay(), 1000),
                    makeArticle(survivedUser.getId(), registerType, emotion, endDate.atStartOfDay(), 2000),
                    makeArticle(survivedUser.getId(), registerType, emotion, endDate.atStartOfDay(), 3000)
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
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderEmotionAmountAverageDto::getEmotion)
                    .containsOnly(emotion);
            assertThat(dtos)
                    .extracting(GenderEmotionAmountAverageDto::getAmountAverage)
                    .containsOnly(amountAverage);
        }

        @Test
        @Order(2)
        void 금액의_평균을_반활할_때_100의_자리수에서_반올림된다() throws Exception {
            // given
            RegisterType registerType = RegisterType.SPEND;
            Emotion emotion = Emotion.PROUD;
            List<Article> articles = List.of(
                    makeArticle(survivedUser.getId(), registerType, emotion, startDate.atStartOfDay(), 1100),
                    makeArticle(survivedUser.getId(), registerType, emotion, endDate.atStartOfDay(), 2200),
                    makeArticle(survivedUser.getId(), registerType, emotion, endDate.atStartOfDay(), 3300)
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
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderEmotionAmountAverageDto::getAmountAverage)
                    .containsOnly(amountAverage);
            assertThat(amountAverage % 1000).isZero();
        }
    }

    @Nested
    class 성별_일별_금액_합을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            RegisterType registerType = RegisterType.SPEND;
            List<Article> filteredArticles = List.of(
                    makeArticle(filteredUser.getId(), registerType, null, startDate.atStartOfDay().minusSeconds(1L), 1000),
                    makeArticle(filteredUser.getId(), registerType, null, endDate.atStartOfDay().plusSeconds(1L), 1000)
            );
            List<Article> survivedArticles = List.of(
                    makeArticle(survivedUser.getId(), registerType, null, startDate.atStartOfDay(), 1000),
                    makeArticle(survivedUser.getId(), registerType, null, endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderDailyAmountSumDto> dtos = genderStatisticsRepository.getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(GenderDailyAmountSumDto::getGender)
                    .distinct()
                    .toList())
                    .containsOnly(survivedGender)
                    .doesNotContain(filteredGender);
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            RegisterType filteredRegisterType = RegisterType.SPEND;
            RegisterType survivedRegisterType = RegisterType.SAVE;
            List<Article> filteredArticles = List.of(
                    makeArticle(filteredUser.getId(), filteredRegisterType, null, startDate.atStartOfDay(), 1000),
                    makeArticle(filteredUser.getId(), filteredRegisterType, null, endDate.atStartOfDay(), 1000)
            );
            List<Article> survivedArticles = List.of(
                    makeArticle(survivedUser.getId(), survivedRegisterType, null, startDate.atStartOfDay(), 1000),
                    makeArticle(survivedUser.getId(), survivedRegisterType, null, endDate.atStartOfDay(), 1000)
            );
            em.flush();

            // when
            List<GenderDailyAmountSumDto> dtos = genderStatisticsRepository.getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(
                    survivedRegisterType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(GenderDailyAmountSumDto::getGender)
                    .distinct()
                    .toList())
                    .containsOnly(survivedGender)
                    .doesNotContain(filteredGender);
        }

        @Test
        void 성별_일별_금액의_합을_반환한다() throws Exception {
            // given
            RegisterType registerType = RegisterType.SPEND;
            Emotion emotion = Emotion.PROUD;
            List<Article> articles = List.of(
                    makeArticle(survivedUser.getId(), registerType, emotion, endDate.atStartOfDay(), 1000),
                    makeArticle(survivedUser.getId(), registerType, emotion, endDate.atStartOfDay(), 2000),
                    makeArticle(survivedUser.getId(), registerType, emotion, endDate.atStartOfDay(), 3000)
            );
            long sum = articles.stream()
                    .mapToLong(Article::getAmount)
                    .reduce(Long::sum).orElseGet(() -> -1L);
            em.flush();

            // when
            List<GenderDailyAmountSumDto> dtos = genderStatisticsRepository.getAmountSumsEachGenderAndDayBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
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
            RegisterType registerType = RegisterType.SPEND;
            String survivedContent = "survivedContent";
            String filteredContent = "filteredContent";
            List<Article> filteredArticles = List.of(
                    makeArticle(filteredUser.getId(), registerType, null, startDate.atStartOfDay().minusSeconds(1L), filteredContent),
                    makeArticle(filteredUser.getId(), registerType, null, endDate.atStartOfDay().plusSeconds(1L), filteredContent)
            );
            List<Article> survivedArticles = List.of(
                    makeArticle(survivedUser.getId(), registerType, null, startDate.atStartOfDay(), survivedContent),
                    makeArticle(survivedUser.getId(), registerType, null, endDate.atStartOfDay(), survivedContent)
            );
            em.flush();

            // when
            List<MemoDto> dtos = genderStatisticsRepository.getAllMemosByGenderBetweenStartDateAndEndDate(
                    Gender.MALE,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MemoDto::getContent)
                    .containsOnly(survivedContent)
                    .doesNotContain(filteredContent);
        }

        @Test
        void 입력한_성별을_가지고_있는_유저들의_메모만_반환한다() throws Exception {
            // given
            String survivedContent = "survivedContent";
            String filteredContent = "filteredContent";
            List<Article> filteredArticles = List.of(
                    makeArticle(filteredUser.getId(), RegisterType.SPEND, null, startDate.atStartOfDay(), filteredContent),
                    makeArticle(filteredUser.getId(), RegisterType.SPEND, null, endDate.atStartOfDay(), filteredContent)
            );
            List<Article> survivedArticles = List.of(
                    makeArticle(survivedUser.getId(), RegisterType.SPEND, null, startDate.atStartOfDay(), survivedContent),
                    makeArticle(survivedUser.getId(), RegisterType.SPEND, null, endDate.atStartOfDay(), survivedContent)
            );
            em.flush();

            // when
            List<MemoDto> dtos = genderStatisticsRepository.getAllMemosByGenderBetweenStartDateAndEndDate(
                    Gender.MALE,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MemoDto::getContent)
                    .containsOnly(survivedContent)
                    .doesNotContain(filteredContent);
        }
    }
    @Nested
    class 성별_만족도_평균을_반환하는_메서드 {
        @Test
        void 입력받은_startDate와_endDate를_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            RegisterType registerType = RegisterType.SPEND;
            List<Article> filteredArticles = List.of(
                    makeArticle(filteredUser.getId(), registerType, null, startDate.atStartOfDay().minusSeconds(1L), 1.0f),
                    makeArticle(filteredUser.getId(), registerType, null, endDate.atStartOfDay().plusSeconds(1L), 1.0f)
            );
            List<Article> survivedArticles = List.of(
                    makeArticle(survivedUser.getId(), registerType, null, startDate.atStartOfDay(), 1.0f),
                    makeArticle(survivedUser.getId(), registerType, null, endDate.atStartOfDay(), 1.0f)
            );
            em.flush();

            // when
            List<GenderSatisfactionAverageDto> dtos = genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderSatisfactionAverageDto::getGender)
                    .contains(survivedGender)
                    .doesNotContain(filteredGender);
        }

        @Test
        void 입력받은_registerType을_기준으로_데이터를_필터링해서_반환한다() throws Exception {
            // given
            RegisterType filteredRegisterType = RegisterType.SAVE;
            RegisterType survivedRegisterType = RegisterType.SPEND;
            List<Article> removedArticles = List.of(
                    makeArticle(filteredUser.getId(), filteredRegisterType, null, startDate.atStartOfDay(), 1.0f),
                    makeArticle(filteredUser.getId(), filteredRegisterType, null, endDate.atStartOfDay(), 1.0f)
            );
            List<Article> articles = List.of(
                    makeArticle(survivedUser.getId(), survivedRegisterType, null, startDate.atStartOfDay(), 1.0f),
                    makeArticle(survivedUser.getId(), survivedRegisterType, null, endDate.atStartOfDay(), 1.0f)
            );
            em.flush();

            // when
            List<GenderSatisfactionAverageDto> dtos = genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                    survivedRegisterType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(GenderSatisfactionAverageDto::getGender)
                    .contains(survivedGender)
                    .doesNotContain(filteredGender);
        }

        @Test
        @Order(1)
        void 성별_만족도의_평균을_반환한다() throws Exception {
            // given
            RegisterType registerType = RegisterType.SPEND;
            List<Article> articles = List.of(
                    makeArticle(survivedUser.getId(), registerType, null, startDate.atStartOfDay(), 3.0f),
                    makeArticle(survivedUser.getId(), registerType, null, endDate.atStartOfDay(), 4.0f)
            );
            Float satisfactionAverage = articles.stream()
                    .map(Article::getSatisfaction)
                    .reduce(Float::sum)
                    .orElseGet(() -> 0f)
                    / articles.size();
            em.flush();

            // when
            List<GenderSatisfactionAverageDto> dtos = genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(
                    dtos.stream()
                            .map(GenderSatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(satisfactionAverage);
        }

        @Test
        @Order(2)
        void 만족도의_평균을_소수_둘째_자리에서_반올림해서_반환한다() throws Exception {
            // given
            RegisterType registerType = RegisterType.SPEND;
            List<Article> articles = List.of(
                    makeArticle(survivedUser.getId(), registerType, null, startDate.atStartOfDay(), 3.04f),
                    makeArticle(survivedUser.getId(), registerType, null, endDate.atStartOfDay(), 4.08f)
            );
            em.flush();

            // when
            List<GenderSatisfactionAverageDto> dtos = genderStatisticsRepository.getSatisfactionAveragesEachGenderBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(
                    dtos.stream()
                            .map(GenderSatisfactionAverageDto::getSatisfactionAverage)
                            .toList()
            ).containsOnly(3.6f);
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

    /**
     * Long 타입 변수를 받아 100의 자리에서 반올림하는 메서드
     * */
    private static Long roundingAverage(Long amountAverage) {
        amountAverage += 500L;
        amountAverage = amountAverage - (amountAverage % 1000);
        return amountAverage;
    }
}