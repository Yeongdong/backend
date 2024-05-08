package com.example.spinlog.article.service;

import com.example.spinlog.article.dto.*;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.article.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void 게시글_작성_테스트() {
        // Given
        WriteArticleRequestDTO requestDTO = WriteArticleRequestDTO.builder()
                .content("Test Thing")
                .event("Test event")
                .thought("Test thought")
                .emotion("ANNOYED")
                .result("Test result")
                .satisfaction(5F)
                .reason("Test Reason")
                .improvements("Test Improvements")
                .aiComment("Test AiComment")
                .amount(100)
                .registerType("SPEND")
                .build();

        // When
        WriteArticleResponseDTO responseDTO = articleService.createArticle(requestDTO);

        // Then
        assertThat(responseDTO).isNotNull();
    }

    //    @Test
//    public void 게시글_리스트_조회_테스트() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//        SearchCond searchCond = new SearchCond("절약", "짜증", "20240430", 5F, "Test word");
//
//        // When
//        Page<ViewArticleResponseDTO> resultPage = articleService.listArticles(pageable, searchCond);
//
//        // Then
//        assertThat(resultPage).isNotNull();
//        assertThat(resultPage.getContent()).isNotEmpty();
//    }
//
    @Test
    public void 게시글_1개_조회_테스트() {
        // Given
        Article article = Article.builder()
                .build();
        articleRepository.save(article);

        // When
        ViewArticleResponseDTO responseDTO = articleService.getArticle(article.getArticleId());

        // Then
        assertThat(responseDTO).isNotNull();
    }

    @Test
    public void 게시글_조회_실패_테스트() {
        // Given
        Long articleId = 999L; // 존재하지 않는 게시글 ID

        // Then
        assertThatThrownBy(() -> articleService.getArticle(articleId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void 게시글_수정_테스트() {
        // Given
        Article article = Article.builder()
                .emotion(Emotion.ANNOYED)
                .registerType(RegisterType.SPEND)
                .build();
        articleRepository.save(article);
        UpdateArticleRequestDTO updateDTO = UpdateArticleRequestDTO.builder()
                .emotion(Emotion.SAD.toString())
                .registerType(RegisterType.SAVE.toString())
                .build();

        // When
        UpdateArticleResponseDTO responseDTO = articleService.updateArticle(article.getArticleId(), updateDTO);

        // Then
        assertThat(responseDTO).isNotNull();
    }

    @Test
    public void 게시글_삭제_테스트() {
        // Given
        Article article = Article.builder()
                .build();
        articleRepository.save(article);

        // When
        articleService.deleteArticle(article.getArticleId());

        // Then
        assertThat(articleRepository.existsById(article.getArticleId())).isFalse();
    }
}
