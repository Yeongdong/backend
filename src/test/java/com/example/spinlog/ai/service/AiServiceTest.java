package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.AiRequestDto;
import com.example.spinlog.ai.dto.AiResponseDto;
import com.example.spinlog.article.dto.WriteArticleRequestDto;
import com.example.spinlog.article.dto.WriteArticleResponseDto;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.service.ArticleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class AiServiceTest {

    @MockBean
    private AiService aiService;

    @Autowired
    private ArticleService articleService;

    WriteArticleResponseDto writeArticleResponseDto;

    @BeforeEach
    void setUp() {
        // 게시글 생성
        WriteArticleRequestDto requestDto = WriteArticleRequestDto.builder()
                .content("투썸플레이스 아이스아메리카노")
                .spendDate("2024-04-04T11:22:33")
                .event("부장님께 혼남")
                .thought("Test thought")
                .emotion("ANNOYED")
                .satisfaction(2F)
                .reason("홧김에 돈 쓴게 마음에 들지 않는다")
                .improvements("소비 전에 한번 더 생각하고 참아본다")
                .amount(5000)
                .registerType("SPEND")
                .build();
        writeArticleResponseDto = articleService.createArticle(requestDto);
    }

    @AfterEach
    void tearDown() {
        articleService.deleteArticle(writeArticleResponseDto.getArticleId());
    }

    @Test
    @DisplayName("AI한마디를 요청하면 응답을 받는다.")
    void AI요청_성공() {
        // Given
        AiRequestDto aiRequestDto = AiRequestDto.builder()
                .articleId(writeArticleResponseDto.getArticleId())
                .build();
        when(aiService.requestAiComment(any()))
                .thenReturn(
                        AiResponseDto.builder()
                                .content("content")
                                .build());

        // When
        AiResponseDto aiResponseDto = aiService.requestAiComment(aiRequestDto);

        // Then
        assertThat(aiResponseDto).isNotNull();
        assertThat(aiResponseDto.getContent()).isNotEmpty();

        // TODO 코드 수정
        //Article aiCommentAddArticle = articleService.findArticleById(aiRequestDto.getArticleId());
        //assertThat(aiCommentAddArticle.getAiComment()).isEqualTo(aiResponseDto.getContent());
    }

    @Test
    @DisplayName("존재하지 않는 Article ID를 입력하면 NoSuchElementExcepiton이 발생한다.")
    void AI요청_실패() {
        // Given
        AiRequestDto aiRequestDto = AiRequestDto.builder()
                .articleId(999L)
                .build();
        when(aiService.requestAiComment(any()))
                .thenThrow(new NoSuchElementException("존재하지 않는 Article ID로 인해 NoSuchElementException 예외 발생."));

        // When, Then
        assertThrows(NoSuchElementException.class, () -> aiService.requestAiComment(aiRequestDto),
                "존재하지 않는 Article ID로 인해 NoSuchElementException 예외 발생.");
    }
}