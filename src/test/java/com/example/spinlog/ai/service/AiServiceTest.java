package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.AiRequestDto;
import com.example.spinlog.ai.dto.AiResponseDto;
import com.example.spinlog.article.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiServiceTest {

    @Autowired
    private AiService aiService;

    @Autowired
    private OpenAiClient openAiClient;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ModelMapper modelMapper;



    @Test
    void requestAiComment_ShouldReturnAiResponseDto() {
        // 준비
        Long articleId = 1L;
        AiRequestDto requestDto = new AiRequestDto();
        requestDto.setArticleId(articleId);
        // 다른 요청 데이터도 준비할 수 있습니다.

        // 기대 값 설정
        String expectedAiComment = "AI 코멘트";
        AiResponseDto expectedResponseDto = new AiResponseDto();
        expectedResponseDto.setContent(expectedAiComment);

        // 메서드 호출
        AiResponseDto result = aiService.requestAiComment(requestDto);

        // 결과 검증
        assertEquals(expectedResponseDto.getContent(), result.getContent(), "AI 응답이 예상 값과 일치하는지 확인");
    }

    @Test
    void requestAiComment_WhenOpenAiClientReturnsEmptyChoice_ThrowsException() {
        // 준비
        AiRequestDto requestDto = new AiRequestDto();
        requestDto.setArticleId(1L);

        // 메서드 호출 및 예외 검증
        assertThrows(IllegalStateException.class, () -> aiService.requestAiComment(requestDto),
                "OpenAiClient가 빈 Choice를 반환할 경우 IllegalStateException 예외가 발생해야 합니다.");
    }

    @Test
    void requestAiComment_WhenArticleNotFound_ThrowsException() {
        // 준비
        AiRequestDto requestDto = new AiRequestDto();
        requestDto.setArticleId(9999L); // 존재하지 않는 articleId 설정

        // 메서드 호출 및 예외 검증
        assertThrows(IllegalArgumentException.class, () -> aiService.requestAiComment(requestDto),
                "존재하지 않는 Article ID로 인해 IllegalArgumentException 예외가 발생해야 합니다.");
    }
}