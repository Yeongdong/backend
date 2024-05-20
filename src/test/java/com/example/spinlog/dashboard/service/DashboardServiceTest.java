package com.example.spinlog.dashboard.service;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.dashboard.dto.DashboardResponseDto;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    }

    @Test
    void 게시글_빈값_조회() {
        // Given
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

        assertThat(responseDto.getSatisfactionAverage()).isEqualTo(3.75F);
        assertThat(responseDto.getEmotionAmountTotal().size()).isEqualTo(2);
        assertThat(responseDto.getDailyAmount().size()).isEqualTo(2);

        verify(userRepository, times(1)).findByAuthenticationName("testUser");
    }
}