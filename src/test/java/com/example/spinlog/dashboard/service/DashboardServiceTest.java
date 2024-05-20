package com.example.spinlog.dashboard.service;

import com.example.spinlog.article.dto.ViewArticleResponseDto;
import com.example.spinlog.article.dto.WriteArticleRequestDto;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.dashboard.dto.DashboardResponseDto;
import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.user.custom.securitycontext.WithMockCustomOAuth2User;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import com.example.spinlog.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.spinlog.user.custom.securitycontext.OAuth2Provider.KAKAO;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private User user;
    private Article article1;
    private Article article2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .mbti(Mbti.ISTP)
                .gender(Gender.MALE)
                .budget(12345)
                .authenticationName("testUser")
                .build();

        article1 = Article.builder()
                .user(user)
                .spendDate(LocalDateTime.of(2024, 5, 1, 0, 0))
                .content("Test content")
                .event("Test event")
                .thought("Test thought")
                .emotion(null)
                .reason("Test Reason")
                .satisfaction(0F)
                .improvements("Test Improvements")
                .amount(null)
                .registerType(RegisterType.valueOf("SPEND"))
                .build();

        user.addArticle(article1);
    }

    @Test
    void 게시글_빈값_조회() {
        // Given
        when(userRepository.findByAuthenticationName("testUser")).thenReturn(Optional.ofNullable(user));

        DashboardResponseDto responseDto = dashboardService.requestData("testUser", "20240510", "SAVE");

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getSatisfactionAverage()).isEqualTo(0);
        assertThat(responseDto.getEmotionAmountTotal()).isEqualTo(List.of());
        assertThat(responseDto.getDailyAmount()).isEqualTo(List.of());
        log.info(responseDto.toString());

        verify(userRepository, times(1)).findByAuthenticationName("testUser");
    }

    @Test
    public void testRequestData_Success() {
        Article article3 = Article.builder()
                .user(user)
                .spendDate(LocalDateTime.of(2024, 5, 1, 0, 0))
                .content("Test content")
                .event("Test event")
                .thought("Test thought")
                .emotion(Emotion.valueOf("SAD"))
                .reason("Test Reason")
                .satisfaction(4.5F)
                .improvements("Test Improvements")
                .amount(100)
                .registerType(RegisterType.valueOf("SPEND"))
                .build();

        Article article4 = Article.builder()
                .user(user)
                .spendDate(LocalDateTime.of(2024, 5, 2, 0, 0))
                .content("Test content")
                .event("Test event")
                .thought("Test thought")
                .emotion(Emotion.valueOf("ANNOYED"))
                .reason("Test Reason")
                .satisfaction(3.0F)
                .improvements("Test Improvements")
                .amount(50)
                .registerType(RegisterType.valueOf("SPEND"))
                .build();

        user.addArticle(article3);
        user.addArticle(article4);
        when(userRepository.findByAuthenticationName("testUser")).thenReturn(Optional.ofNullable(user));

        DashboardResponseDto responseDto = dashboardService.requestData("testUser", "20240510", "SPEND");

        assertEquals(3.75f, responseDto.getSatisfactionAverage());
        assertEquals(2, responseDto.getEmotionAmountTotal().size());
        assertEquals(2, responseDto.getDailyAmount().size());

        verify(userRepository, times(1)).findByAuthenticationName("testUser");
    }
}