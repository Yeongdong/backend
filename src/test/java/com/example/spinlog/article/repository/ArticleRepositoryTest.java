package com.example.spinlog.article.repository;

import com.example.spinlog.article.service.request.ArticleUpdateRequest;
import com.example.spinlog.article.service.request.SearchCond;
import com.example.spinlog.article.dto.ViewArticleSumDto;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.spinlog.article.entity.Emotion.ANNOYED;
import static com.example.spinlog.article.entity.Emotion.SAD;
import static com.example.spinlog.article.entity.RegisterType.SAVE;
import static com.example.spinlog.article.entity.RegisterType.SPEND;
import static com.example.spinlog.user.entity.Gender.MALE;
import static com.example.spinlog.user.entity.Mbti.ISTJ;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("일기를 저장하면 ID를 반환한다.")
    @Test
    void create() {
        // given
        Article article = createArticle();

        // when
        articleRepository.save(article);

        // then
        assertThat(article.getArticleId()).isNotNull();
    }

    @DisplayName("일기 저장시 필수값이 없으면 에러를 반환한다.")
    @Test
    void create_fail() {
        // given
        Article article = Article.builder()
                .content(null)  // 필수 값 누락
                .spendDate(LocalDateTime.of(2024, 5, 30, 0, 0))
                .event("test event")
                .thought("test thought")
                .emotion(ANNOYED)
                .satisfaction(5F)
                .reason(null)
                .improvements(null)
                .aiComment(null)
                .amount(100)
                .registerType(SPEND)
                .build();

        // when then
        assertThatThrownBy(() -> articleRepository.save(article))
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(article.getArticleId()).isNull();
    }

    @DisplayName("일기 번호를 통해 일기를 조회한다.")
    @Test
    void findOne() {
        // given
        Article article = createArticle();
        articleRepository.save(article);

        // when
        Article foundArticle = articleRepository.findById(article.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id " + article.getArticleId()));

        // then
        assertThat(foundArticle).isEqualTo(article);
    }

    @DisplayName("저장되지 않은 일기의 번호를 입력하면 예외를 반환한다.")
    @Test
    void findOne_fail() {
        // given
        Long invalidId = 999L;

        // when then
        assertThatThrownBy(() -> articleRepository.findById(invalidId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id " + invalidId)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Article not found with id " + invalidId);
    }

    @DisplayName("필터없이 조회시 저장된 모든 일기를 반환한다.")
    @Test
    void findAll() {
        // given
        User user = createUser();
        userRepository.save(user);

        Article article1 = createArticle(user);
        Article article2 = createArticle(user);
        Article article3 = createArticle(user);
        Article article4 = createArticle(user);
        articleRepository.saveAll(List.of(article1, article2, article3, article4));

        Pageable pageable = PageRequest.of(0, 10);
        SearchCond cond = SearchCond.builder()
                .registerTypes(List.of(SPEND))
                .emotions(List.of(ANNOYED))
                .satisfactions(List.of(5F))
                .words(List.of(""))
                .build();

        // when
        Page<ViewArticleSumDto> result = articleRepository.search(user, pageable, cond);

        // then
        assertThat(result).hasSize(4)
                .extracting("articleId")
                .containsExactlyInAnyOrder(
                        article1.getArticleId(),
                        article2.getArticleId(),
                        article3.getArticleId(),
                        article4.getArticleId()
                );
    }

    @DisplayName("조회 결과가 아무것도 없으면 null을 반환한다.")
    @Test
    void findAll_fail() {
        // given
        User user = createUser();
        userRepository.save(user);

        Article article1 = createArticle(user);
        Article article2 = createArticle(user);
        Article article3 = createArticle(user);
        Article article4 = createArticle(user);
        articleRepository.saveAll(List.of(article1, article2, article3, article4));

        Pageable pageable = PageRequest.of(0, 10);
        SearchCond cond = SearchCond.builder()
                .registerTypes(List.of(SAVE))
                .emotions(List.of(ANNOYED))
                .satisfactions(List.of(5F))
                .words(List.of(""))
                .build();

        // when
        Page<ViewArticleSumDto> result = articleRepository.search(user, pageable, cond);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("필터에 해당하는 일기만 반환한다.")
    @Test
    void findAll_filtered() {
        // given
        User user = createUser();
        userRepository.save(user);

        Article article1 = createArticle(user);
        Article article2 = createArticle(user);
        Article article3 = Article.builder()
                .user(user)
                .content("test content")
                .spendDate(LocalDateTime.of(2024, 5, 30, 0, 0))
                .event("test event")
                .thought("test thought")
                .emotion(SAD)
                .satisfaction(5F)
                .reason(null)
                .improvements(null)
                .aiComment(null)
                .amount(100)
                .registerType(SAVE)
                .build();
        Article article4 = Article.builder()
                .user(user)
                .content("test content")
                .spendDate(LocalDateTime.of(2024, 5, 30, 0, 0))
                .event("test event")
                .thought("test thought")
                .emotion(SAD)
                .satisfaction(5F)
                .reason(null)
                .improvements(null)
                .aiComment(null)
                .amount(100)
                .registerType(SAVE)
                .build();
        articleRepository.saveAll(List.of(article1, article2, article3, article4));

        Pageable pageable = PageRequest.of(0, 10);
        SearchCond cond = SearchCond.builder()
                .registerTypes(List.of(SAVE))
                .emotions(List.of(SAD))
                .satisfactions(List.of(5F))
                .words(List.of(""))
                .build();

        // when
        Page<ViewArticleSumDto> result = articleRepository.search(user, pageable, cond);

        // then
        assertThat(result).hasSize(2)
                .extracting("articleId", "registerType", "emotion")
                .containsExactlyInAnyOrder(
                        tuple(3L, SAVE, SAD),
                        tuple(4L, SAVE, SAD)
                );
    }

    @DisplayName("해당 유저의 일기만 반환한다.")
    @Test
    void findAll_user_filtered() {
        // given
        User user1 = User.builder()
                .email("test@example.com")
                .mbti(ISTJ)
                .gender(MALE)
                .authenticationName("test user1")
                .build();
        User user2 = User.builder()
                .email("test@example.com")
                .mbti(ISTJ)
                .gender(MALE)
                .authenticationName("test user2")
                .build();
        userRepository.saveAll(List.of(user1, user2));

        Article article1 = createArticle(user1);
        Article article2 = createArticle(user1);
        Article article3 = createArticle(user2);
        Article article4 = createArticle(user2);
        articleRepository.saveAll(List.of(article1, article2, article3, article4));

        Pageable pageable = PageRequest.of(0, 10);
        SearchCond cond = SearchCond.builder()
                .registerTypes(List.of(SPEND))
                .emotions(List.of(ANNOYED))
                .satisfactions(List.of(5F))
                .words(List.of(""))
                .build();

        // when
        Page<ViewArticleSumDto> result = articleRepository.search(user2, pageable, cond);

        // then
        assertThat(result).hasSize(2)
                .extracting("articleId")
                .containsExactlyInAnyOrder(
                        article3.getArticleId(),
                        article4.getArticleId()
                );
    }

    @DisplayName("일기를 업데이트하면 변경된 내용이 저장된다.")
    @Test
    void update() {
        // given
        Article article = createArticle();
        articleRepository.save(article);

        ArticleUpdateRequest updateDto = ArticleUpdateRequest.builder()
                .content("updated content")
                .spendDate("2024-04-04T11:22:33")
                .event("updated event")
                .thought("updated thought")
                .emotion("SAD")
                .satisfaction(1F)
                .reason("updated reason")
                .improvements("updated improvements")
                .amount(2000)
                .registerType("SAVE")
                .build();

        article.update(updateDto);

        // when
        Article updatedArticle = articleRepository.findById(article.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id " + article.getArticleId()));

        // then
        assertThat(updatedArticle.getContent()).isEqualTo("updated content");
        assertThat(updatedArticle.getEmotion()).isEqualTo(SAD);
    }

    @DisplayName("일기를 삭제하면 해당 일기를 더 이상 조회할 수 없다.")
    @Test
    void delete() {
        // given
        Article article = createArticle();
        articleRepository.save(article);

        // when
        articleRepository.delete(article);

        // then
        assertThat(articleRepository.findById(article.getArticleId())).isEmpty();
    }

    private static User createUser() {
        return User.builder()
                .email("test@example.com")
                .mbti(ISTJ)
                .gender(MALE)
                .authenticationName("test user")
                .build();
    }

    private Article createArticle(User user) {
        return Article.builder()
                .user(user)
                .content("test content")
                .spendDate(LocalDateTime.of(2024, 5, 30, 0, 0))
                .event("test event")
                .thought("test thought")
                .emotion(ANNOYED)
                .satisfaction(5F)
                .reason(null)
                .improvements(null)
                .aiComment(null)
                .amount(100)
                .registerType(SPEND)
                .build();
    }

    private static Article createArticle() {
        return Article.builder()
                .content("test content")
                .spendDate(LocalDateTime.of(2024, 5, 30, 0, 0))
                .event("test event")
                .thought("test thought")
                .emotion(ANNOYED)
                .satisfaction(5F)
                .reason(null)
                .improvements(null)
                .aiComment(null)
                .amount(100)
                .registerType(SPEND)
                .build();
    }
}