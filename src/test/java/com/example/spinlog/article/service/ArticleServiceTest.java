package com.example.spinlog.article.service;

import com.example.spinlog.article.dto.*;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.user.custom.securitycontext.WithMockCustomOAuth2User;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static com.example.spinlog.user.custom.securitycontext.OAuth2Provider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class ArticleServiceTest {

    @Autowired  // articleId가 필요해 Autowired 설정
    private ArticleRepository articleRepository;

    @Autowired  // userId가 필요해 Autowired 설정
    private UserRepository userRepository;

    @Autowired  //userId, articleId가 필요해 Autowired 설정
    private ArticleService articleService;

    private User user;
    private Article article;

    @BeforeEach
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void setUp() {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticationName = oAuth2User.getOAuth2Response().getAuthenticationName();
        User buildUser = User.builder()
                .email(oAuth2User.getName())
                .mbti(Mbti.ISTP)
                .gender(Gender.MALE)
                .budget(12345)
                .authenticationName(authenticationName)
                .build();
        user = userRepository.save(buildUser);

        WriteArticleRequestDto requestDto = WriteArticleRequestDto.builder()
                .content("Test Content")
                .spendDate("2024-04-04T11:22:33")
                .event("Test event")
                .thought("Test thought")
                .emotion("ANNOYED")
                .satisfaction(5F)
                .reason("Test Reason")
                .improvements("Test Improvements")
                .amount(100)
                .registerType("SPEND")
                .build();
        article = articleRepository.save(requestDto.toEntity(user));
    }

    @AfterEach
    void tearDown() {
        articleRepository.deleteAll();
    }

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void 게시글_작성() {
        // Given
        WriteArticleRequestDto requestDto = WriteArticleRequestDto.builder()
                .content("Test Content")
                .spendDate("2024-04-04T11:22:33")
                .event("Test event")
                .thought("Test thought")
                .emotion("ANNOYED")
                .satisfaction(5F)
                .reason("Test Reason")
                .improvements("Test Improvements")
                .amount(100)
                .registerType("SPEND")
                .build();

        // When
        WriteArticleResponseDto responseDto = articleService.createArticle(user.getAuthenticationName(), requestDto);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getArticleId()).isNotNull();
        assertThat(articleRepository.findById(responseDto.getArticleId()).isPresent()).isTrue();
    }

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    public void 게시글_리스트_조회_전체_테스트() {
        // Given
        for (int i = 1; i <= 15; i++) {
            Article article1 = Article.builder()
                    .user(user)
                    .spendDate(LocalDateTime.of(2024, 5, 1, 0, 0).plusDays(i))
                    .content("Test Content" + i)
                    .event("Test event" + i)
                    .thought("Test thought" + i)
                    .emotion(Emotion.valueOf("ANNOYED"))
                    .satisfaction(Float.parseFloat(i + ""))
                    .reason("Test Reason" + i)
                    .improvements("Test Improvements" + i)
                    .amount(i * 1000)
                    .registerType(RegisterType.valueOf("SPEND"))
                    .build();
            articleRepository.save(article1);

            Article article2 = Article.builder()
                    .user(user)
                    .spendDate(LocalDateTime.of(2024, 5, 1, 0, 0).plusDays(i))
                    .content("Test Content" + i)
                    .event("Test event" + i)
                    .thought("Test thought" + i)
                    .emotion(Emotion.valueOf("SHY"))
                    .satisfaction(Float.parseFloat(i + ""))
                    .reason("Test Reason" + i)
                    .improvements("Test Improvements" + i)
                    .amount(i * 1000)
                    .registerType(RegisterType.valueOf("SPEND"))
                    .build();
            articleRepository.save(article2);

            Article article3 = Article.builder()
                    .user(user)
                    .spendDate(LocalDateTime.of(2024, 5, 1, 0, 0).plusDays(i))
                    .content("Test Content" + i)
                    .event("Test event" + i)
                    .thought("Test thought" + i)
                    .emotion(Emotion.valueOf("SAD"))
                    .satisfaction(Float.parseFloat(i + ""))
                    .reason("Test Reason" + i)
                    .improvements("Test Improvements" + i)
                    .amount(i * 1000)
                    .registerType(RegisterType.valueOf("SAVE"))
                    .build();
            articleRepository.save(article3);
        }

        Pageable pageable = Pageable.ofSize(10).withPage(0);
        SearchCondRequestDto searchCond = SearchCondRequestDto.builder()
                .build();

        // When
        ViewListResponseDto responseDto = articleService.listArticles(user.getAuthenticationName(), pageable, searchCond);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getSpendList().size()).isEqualTo(46);
    }

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    public void 게시글_리스트_조회_필터_테스트() {
        // Given
        for (int i = 1; i <= 15; i++) {
            Article article1 = Article.builder()
                    .user(user)
                    .spendDate(LocalDateTime.of(2024, 5, 1, 0, 0).plusDays(i))
                    .content("Test Content" + i)
                    .event("Test event" + i)
                    .thought("Test thought" + i)
                    .emotion(Emotion.valueOf("ANNOYED"))
                    .satisfaction(Float.parseFloat(i + ""))
                    .reason("Test Reason" + i)
                    .improvements("Test Improvements" + i)
                    .amount(i * 1000)
                    .registerType(RegisterType.valueOf("SPEND"))
                    .build();
            articleRepository.save(article1);

            Article article2 = Article.builder()
                    .user(user)
                    .spendDate(LocalDateTime.of(2024, 5, 1, 0, 0).plusDays(i))
                    .content("Test Content" + i)
                    .event("Test event" + i)
                    .thought("Test thought" + i)
                    .emotion(Emotion.valueOf("SHY"))
                    .satisfaction(Float.parseFloat(i + ""))
                    .reason("Test Reason" + i)
                    .improvements("Test Improvements" + i)
                    .amount(i * 1000)
                    .registerType(RegisterType.valueOf("SPEND"))
                    .build();
            articleRepository.save(article2);

            Article article3 = Article.builder()
                    .user(user)
                    .spendDate(LocalDateTime.of(2024, 5, 1, 0, 0).plusDays(i))
                    .content("Test Content" + i)
                    .event("Test event" + i)
                    .thought("Test thought" + i)
                    .emotion(Emotion.valueOf("SAD"))
                    .satisfaction(Float.parseFloat(i + ""))
                    .reason("Test Reason" + i)
                    .improvements("Test Improvements" + i)
                    .amount(i * 1000)
                    .registerType(RegisterType.valueOf("SAVE"))
                    .build();
            articleRepository.save(article3);
        }

        Pageable pageable = Pageable.ofSize(10).withPage(0);
        SearchCondRequestDto searchCond = SearchCondRequestDto.builder()
                .registerType("SAVE")
                .emotion("SAD")
                .from("20240501")
                .to("20240515")
                .build();

        // When
        ViewListResponseDto responseDto = articleService.listArticles(user.getAuthenticationName(), pageable, searchCond);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getSpendList().size()).isEqualTo(14);
    }

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void 게시글_1개_조회_성공() {
        // When
        ViewArticleResponseDto responseDto = articleService.getArticle(user.getAuthenticationName(), article.getArticleId());

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getContent()).isEqualTo("Test Content");
    }

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void 존재하지_않는_게시글_조회_실패() {
        // Given
        Long articleId = 999L; // 존재하지 않는 게시글 ID

        // When, Then
        assertThatThrownBy(() -> articleService.getArticle(user.getAuthenticationName(), articleId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void 타_작성자의_게시글_조회_실패() {
        // Given
        CustomOAuth2User oAuth2User = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticationName = "differentAuthenticationName";

        User buildUser = User.builder()
                .email(oAuth2User.getName())
                .mbti(Mbti.ISTP)
                .gender(Gender.MALE)
                .budget(12345)
                .authenticationName(authenticationName)
                .build();
        User diffUser = userRepository.save(buildUser);

        Long articleId = 2L;

        // When
        assertThatThrownBy(() -> articleService.getArticle(diffUser.getAuthenticationName(), articleId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void 게시글_수정() {
        UpdateArticleRequestDto updateDto = UpdateArticleRequestDto.builder()
                .spendDate("2024-05-05T12:34:56")
                .emotion(Emotion.SAD.toString())
                .registerType(RegisterType.SAVE.toString())
                .build();

        // When
        articleService.updateArticle(user.getAuthenticationName(), article.getArticleId(), updateDto);

        // Then
        assertThat(article.getEmotion()).isEqualTo(Emotion.SAD);
        assertThat(article.getRegisterType()).isEqualTo(RegisterType.SAVE);
    }

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void 게시글_삭제() {
        // When
        articleService.deleteArticle(user.getAuthenticationName(), article.getArticleId());

        // Then
        assertThat(articleRepository.existsById(article.getArticleId())).isFalse();
    }
}
