package com.example.spinlog.calendar.repository;

import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.calendar.dto.DaySpend;
import com.example.spinlog.calendar.repository.dto.MonthSpendDto;
import com.example.spinlog.util.ArticleFactory;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CalenderRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    CalenderRepository calenderRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        calenderRepository = new CalenderRepository(em);
    }

    @Nested
    class getMonthSpendList{
        @Test
        void 파라미터로_받은_userId에_해당하는_Article들만_조회된다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("email1")
                            .authenticationName("name1")
                            .build()
            );
            User another = userRepository.save(
                    User.builder()
                            .email("email2")
                            .authenticationName("name2")
                            .build()
            );
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SPEND)
                            .build()
                            .createArticle()));
            another.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(another)
                            .registerType(RegisterType.SAVE)
                            .build()
                            .createArticle()));

            // when
            List<MonthSpendDto> monthSpendList = calenderRepository.getMonthSpendList(user.getId(), LocalDate.now());

            // then
            assertThat(monthSpendList.size()).isEqualTo(1);
            assertThat(monthSpendList)
                    .extracting("registerType")
                    .containsExactly(RegisterType.SPEND);
        }

        @Test
        void 파라미터로_받은_date의_연도와_월에_해당하는_Article들만_조회된다() throws Exception {
            // given
            LocalDate targetDate = LocalDate.of(2021, 1, 1);
            User user = userRepository.save(
                    User.builder()
                            .email("email1")
                            .authenticationName("name1")
                            .build()
            );
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SPEND)
                            .spendDate(targetDate.atStartOfDay())
                            .build()
                            .createArticle()));
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SPEND)
                            .spendDate(targetDate.plusMonths(1L).minusDays(1L).atStartOfDay())
                            .build()
                            .createArticle()));
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SAVE)
                            .spendDate(targetDate.minusDays(1L).atStartOfDay())
                            .build()
                            .createArticle()));
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SAVE)
                            .spendDate(targetDate.plusMonths(1L).atStartOfDay())
                            .build()
                            .createArticle()));

            // when
            List<MonthSpendDto> monthSpendList = calenderRepository.getMonthSpendList(user.getId(), targetDate);

            // then
            assertThat(monthSpendList.size()).isEqualTo(2);
            assertThat(monthSpendList)
                    .extracting("registerType")
                    .containsOnly(RegisterType.SPEND);
        }


    }

    @Nested
    class getDaySpendList{
        @Test
        void 파라미터로_받은_userId에_해당하는_Article들만_조회된다() throws Exception {
            // given
            User user = userRepository.save(
                    User.builder()
                            .email("email1")
                            .authenticationName("name1")
                            .build()
            );
            User another = userRepository.save(
                    User.builder()
                            .email("email2")
                            .authenticationName("name2")
                            .build()
            );
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SPEND)
                            .build()
                            .createArticle()));
            another.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(another)
                            .registerType(RegisterType.SAVE)
                            .build()
                            .createArticle()));

            // when
            List<DaySpend> daySpends = calenderRepository.getDaySpendList(user.getId(), LocalDate.now());

            // then
            assertThat(daySpends.size()).isEqualTo(1);
            assertThat(daySpends)
                    .extracting("registerType")
                    .containsOnly(RegisterType.SPEND);
        }

        @Test
        void 파라미터로_받은_date에_해당하는_Article들만_조회된다() throws Exception {
            // given
            LocalDate targetDate = LocalDate.of(2021, 1, 1);
            User user = userRepository.save(
                    User.builder()
                            .email("email1")
                            .authenticationName("name1")
                            .build()
            );
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SPEND)
                            .spendDate(targetDate.atStartOfDay())
                            .build()
                            .createArticle()));
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SPEND)
                            .spendDate(targetDate.plusDays(1L).atStartOfDay().minusSeconds(1L))
                            .build()
                            .createArticle()));
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SAVE)
                            .spendDate(targetDate.atStartOfDay().minusSeconds(1L))
                            .build()
                            .createArticle()));
            user.addArticle(articleRepository.save(
                    ArticleFactory.builder()
                            .user(user)
                            .registerType(RegisterType.SAVE)
                            .spendDate(targetDate.plusDays(1L).atStartOfDay())
                            .build()
                            .createArticle()));

            // when
            List<DaySpend> daySpends = calenderRepository.getDaySpendList(user.getId(), targetDate);

            // then
            assertThat(daySpends.size()).isEqualTo(2);
            assertThat(daySpends)
                    .extracting("registerType")
                    .containsOnly(RegisterType.SPEND);
        }

    }

}