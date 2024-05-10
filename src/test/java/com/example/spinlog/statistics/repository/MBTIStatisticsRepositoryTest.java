package com.example.spinlog.statistics.repository;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.global.entity.BaseTimeEntity;
import com.example.spinlog.statistics.entity.MBTIFactor;
import com.example.spinlog.statistics.repository.dto.MBTIDailyAmountSumDto;
import com.example.spinlog.statistics.repository.dto.MBTIEmotionAmountAverageDto;
import com.example.spinlog.statistics.repository.dto.MBTISatisfactionAverageDto;
import com.example.spinlog.statistics.repository.dto.MemoDto;
import com.example.spinlog.statistics.required_have_to_delete.UserRepository;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MBTIStatisticsRepositoryTest {
    @Autowired
    MBTIStatisticsRepository mbtiStatisticsRepository;

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em; // flush 하기 위해 필요 (JPA 쓰기 지연 방지)


    /**
     * DB가 데이터를 필터링 해서 반환하는데, 필터링 됐음을 유저의 MBTI로 구분한다.
     * ISTJ -> 필터링 되지 않고 그대로 받환되는 데이터의 MBTI
     * ENFP -> 필터링 되어 받을 수 없는 데이터의 MBTI
     * */
    User survivedUser;
    Mbti survivedMBTI = Mbti.ISTJ;
    List<MBTIFactor> survivedMBTIFactors = List.of(
            MBTIFactor.I, MBTIFactor.S, MBTIFactor.T, MBTIFactor.J
    );
    User filteredUser;
    Mbti filteredMBTI = Mbti.ENFP;
    List<MBTIFactor> filteredMBTIFactors = List.of(
            MBTIFactor.E, MBTIFactor.N, MBTIFactor.F, MBTIFactor.P
    );

    LocalDate startDate, endDate;

    /**
     * JPA에서 등록된 엔티티를 테이블로 생성 해주지만, 뷰는 생성 안되서, sql 파일로 직접 생성
     * */
    @BeforeAll
    public static void createView(@Autowired DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("view/before-test-schema.sql"));
        populator.execute(dataSource);
    }

    /**
     * 필터링 결과를 구분하기 위해, 필터링 대상 MBTI를 가지고 있는 유저 객체 2개를 생성한다.
     * */
    @BeforeEach
    public void createTwoUserToDivideFilteringResultAndLocalDateRange() {
        survivedUser = User.builder()
                .email("survived@email")
                .name("survivedUser")
                .mbti(survivedMBTI)
                .gender(Gender.MALE)
                .build();
        filteredUser = User.builder()
                .email("filtered@email")
                .name("filteredUser")
                .mbti(filteredMBTI) // 정반대의 MBTI 입력 -> 이 MBTI는 필터링 되는 MBTI
                .gender(Gender.FEMALE)
                .build();
        userRepository.save(survivedUser);
        userRepository.save(filteredUser);

        startDate = LocalDate.now().minusDays(2);
        endDate = LocalDate.now().minusDays(1);
    }

    @Nested
    class getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate {
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
            List<MBTIEmotionAmountAverageDto> dtos = mbtiStatisticsRepository.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(MBTIEmotionAmountAverageDto::getMbtiFactor)
                    .distinct()
                    .toList())
                    .containsExactlyInAnyOrderElementsOf(survivedMBTIFactors)
                    .doesNotContainAnyElementsOf(filteredMBTIFactors);
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
            List<MBTIEmotionAmountAverageDto> dtos = mbtiStatisticsRepository.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                    survivedRegisterType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(MBTIEmotionAmountAverageDto::getMbtiFactor)
                    .distinct()
                    .toList())
                    .containsExactlyInAnyOrderElementsOf(survivedMBTIFactors)
                    .doesNotContainAnyElementsOf(filteredMBTIFactors);
        }

        @Test
        @Order(1)
        void MBTI별_감정별_금액의_평균을_반환한다() throws Exception {
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
            List<MBTIEmotionAmountAverageDto> dtos = mbtiStatisticsRepository.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getEmotion)
                    .containsOnly(emotion);
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getAmountAverage)
                    .containsOnly(amountAverage);
        }

        @Test
        @Order(2)
        void 금액의_평균을_반활할_때_100의_자리수에서_반올림_한다() throws Exception {
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
            List<MBTIEmotionAmountAverageDto> dtos = mbtiStatisticsRepository.getAmountAveragesEachMBTIAndEmotionBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTIEmotionAmountAverageDto::getAmountAverage)
                    .containsOnly(amountAverage);
            assertThat(amountAverage % 1000).isZero();
        }
    }

    @Nested
    class getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate {
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
            List<MBTIDailyAmountSumDto> dtos = mbtiStatisticsRepository.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(MBTIDailyAmountSumDto::getMbtiFactor)
                    .distinct()
                    .toList())
                    .containsExactlyInAnyOrderElementsOf(survivedMBTIFactors)
                    .doesNotContainAnyElementsOf(filteredMBTIFactors);
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
            List<MBTIDailyAmountSumDto> dtos = mbtiStatisticsRepository.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                    survivedRegisterType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(MBTIDailyAmountSumDto::getMbtiFactor)
                    .distinct()
                    .toList())
                    .containsExactlyInAnyOrderElementsOf(survivedMBTIFactors)
                    .doesNotContainAnyElementsOf(filteredMBTIFactors);
        }

        @Test
        void MBTI별_일별_금액의_합을_반환한다() throws Exception {
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
            List<MBTIDailyAmountSumDto> dtos = mbtiStatisticsRepository.getAmountSumsEachMBTIAndDayBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MBTIDailyAmountSumDto::getAmountSum)
                    .containsOnly(sum);
        }
    }
    @Nested
    class getAllMemosByMBTIBetweenStartDateAndEndDate {
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
            List<MemoDto> dtos = mbtiStatisticsRepository.getAllMemosByMBTIBetweenStartDateAndEndDate(
                    Mbti.NONE.toString(),
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
        void MBTI_파라미터에_해당하는_MBTI_유저들의_메모가_반환된다() throws Exception {
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
            List<MemoDto> dtos = mbtiStatisticsRepository.getAllMemosByMBTIBetweenStartDateAndEndDate(
                    survivedUser.getMbti().toString(),
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
        void MBTI_파라미터로_NONE을_입력하면_모든_유저들의_메모가_반환된다() throws Exception {
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
            List<MemoDto> dtos = mbtiStatisticsRepository.getAllMemosByMBTIBetweenStartDateAndEndDate(
                    Mbti.NONE.toString(),
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos)
                    .extracting(MemoDto::getContent)
                    .containsAnyElementsOf(
                            filteredArticles.stream()
                                    .map(Article::getContent)
                                    .toList())
                    .containsAnyElementsOf(
                            survivedArticles.stream()
                                    .map(Article::getContent)
                                    .toList());
        }

    }

    @Nested
    class getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate {
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
            List<MBTISatisfactionAverageDto> dtos = mbtiStatisticsRepository.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(MBTISatisfactionAverageDto::getMbtiFactor)
                    .distinct()
                    .toList())
                    .containsExactlyInAnyOrderElementsOf(survivedMBTIFactors)
                    .doesNotContainAnyElementsOf(filteredMBTIFactors);
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
            List<MBTISatisfactionAverageDto> dtos = mbtiStatisticsRepository.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                    survivedRegisterType,
                    startDate,
                    endDate
            );

            // then
            assertThat(dtos.stream()
                    .map(MBTISatisfactionAverageDto::getMbtiFactor)
                    .distinct()
                    .toList())
                    .containsExactlyInAnyOrderElementsOf(survivedMBTIFactors)
                    .doesNotContainAnyElementsOf(filteredMBTIFactors);
        }

        @Test
        @Order(1)
        void MBTI별_만족도의_평균을_반환한다() throws Exception {
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
            List<MBTISatisfactionAverageDto> dtos = mbtiStatisticsRepository.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(
                    dtos.stream()
                            .map(MBTISatisfactionAverageDto::getSatisfactionAverage)
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
            List<MBTISatisfactionAverageDto> dtos = mbtiStatisticsRepository.getSatisfactionAveragesEachMBTIBetweenStartDateAndEndDate(
                    registerType,
                    startDate,
                    endDate
            );

            // then
            assertThat(
                    dtos.stream()
                            .map(MBTISatisfactionAverageDto::getSatisfactionAverage)
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

        // TODO spendDate 머지 후, 수정 필요
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