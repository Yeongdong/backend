package com.example.spinlog.article.controller;

import com.example.spinlog.article.controller.request.UpdateArticleRequestDto;
import com.example.spinlog.article.controller.request.WriteArticleRequestDto;
import com.example.spinlog.article.service.ArticleService;
import com.example.spinlog.article.service.request.ArticleCreateRequest;
import com.example.spinlog.article.service.request.SearchCond;
import com.example.spinlog.article.service.response.ViewArticleResponseDto;
import com.example.spinlog.article.service.response.ViewArticleSumDto;
import com.example.spinlog.article.service.response.ViewListResponseDto;
import com.example.spinlog.article.service.response.WriteArticleResponseDto;
import com.example.spinlog.user.custom.securitycontext.WithMockCustomOAuth2User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.example.spinlog.article.entity.Emotion.*;
import static com.example.spinlog.article.entity.RegisterType.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(ArticleController.class)
@WithMockCustomOAuth2User
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    @DisplayName("새로운 일기를 등록한다.")
    @Test
    void create() throws Exception {
        // given
        WriteArticleRequestDto requestDto = createWriteRequest();
        WriteArticleResponseDto responseDto = createWriteResponse();
        when(articleService.createArticle(anyString(), any(ArticleCreateRequest.class))).thenReturn(responseDto);

        // when
        mockMvc.perform(post("/api/articles")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(() -> "test user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("게시글 작성 성공"));

        // then
        verify(articleService).createArticle(anyString(), any(ArticleCreateRequest.class));
    }

    @DisplayName("게시글 리스트를 불러온다.")
    @Test
    void viewList() throws Exception {
        // given
        ViewArticleSumDto viewArticleSumDto = createViewArticleSumDto();
        ViewListResponseDto responseDto = createViewListResponseDto(viewArticleSumDto);

        when(articleService.listArticles(anyString(), any(Pageable.class), any(SearchCond.class))).thenReturn(responseDto);

        // when
        mockMvc.perform(get("/api/articles")
                        .param("page", "0")
                        .param("size", "10")
                        .param("searchCond", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("게시글 리스트 불러오기 성공"))
                .andExpect(jsonPath("$.data.nextPage").value(false))
                .andExpect(jsonPath("$.data.spendList").isNotEmpty());

        // then
        verify(articleService).listArticles(anyString(), any(Pageable.class), any(SearchCond.class));
    }

    @DisplayName("게시글 1개를 불러온다.")
    @Test
    void viewDetails() throws Exception {
        // given
        ViewArticleResponseDto viewArticleResponseDto = createViewArticleResponseDto();
        when(articleService.getArticle(anyString(), anyLong())).thenReturn(viewArticleResponseDto);

        // when
        mockMvc.perform(get("/api/articles/{articleId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("게시글 1개 불러오기 성공"))
                .andExpect(jsonPath("$.data").exists());

        // then
        verify(articleService).getArticle(anyString(), anyLong());
    }

    @DisplayName("게시글을 업데이트한다.")
    @Test
    void updateArticle() throws Exception {
        // given
        Long articleId = 1L;
        UpdateArticleRequestDto updateArticleRequestDto = createUpdateArticleRequestDto();

        doNothing().when(articleService).updateArticle(anyString(), anyLong(), any());

        // when
        mockMvc.perform(patch("/api/articles/{articleId}", articleId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateArticleRequestDto)))
                .andExpect(status().isOk());

        // then
        verify(articleService).updateArticle(anyString(), anyLong(), any());
    }


    @DisplayName("게시글을 삭제한다.")
    @Test
    void deleteArticle() throws Exception {
        // given
        Long articleId = 1L;
        doNothing().when(articleService).deleteArticle(anyString(), anyLong());

        // when
        mockMvc.perform(delete("/api/articles/{articleId}", articleId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("게시글 삭제 성공"));
    }

    private WriteArticleResponseDto createWriteResponse() {
        return WriteArticleResponseDto.builder()
                .articleId(1L)
                .build();
    }

    private WriteArticleRequestDto createWriteRequest() {
        return WriteArticleRequestDto.builder()
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
    }

    private ViewListResponseDto createViewListResponseDto(ViewArticleSumDto viewArticleSumDto) {
        return ViewListResponseDto.builder()
                .nextPage(false)
                .spendList(Collections.singletonList(viewArticleSumDto))
                .build();
    }

    private ViewArticleSumDto createViewArticleSumDto() {
        return ViewArticleSumDto.builder()
                .articleId(1L)
                .content("test content")
                .emotion(SAD)
                .satisfaction(5F)
                .amount(100)
                .registerType(SPEND)
                .build();
    }

    private ViewArticleResponseDto createViewArticleResponseDto() {
        return ViewArticleResponseDto.builder()
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
    }

    private UpdateArticleRequestDto createUpdateArticleRequestDto() {
        return UpdateArticleRequestDto.builder()
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
    }
}
