package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.*;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.service.ArticleService;
import com.example.spinlog.global.error.exception.BusinessException;
import com.example.spinlog.global.error.exception.ErrorCode;
import com.example.spinlog.global.error.exception.ai.EmptyCommentException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final OpenAiClient openAiClient;
    private final ArticleService articleService;
    private final ModelMapper modelMapper;

    private final static String AI_MODEL = "gpt-3.5-turbo";
    private final static String AI_ROLE = "system";
    private final static String USER_ROLE = "user";
    private final static String MESSAGE_TO_AI = "Your role is to give advice. The data I sent you is from a person in their 20s or 30s who made a purchase due to emotional expression. " +
            "You need to provide empathy and advice based on the data. Please follow the rules below when responding.\n" +
            "1. Include an empathetic response based on the user's provided emotion, event, thoughts, amount, and spending details (or saving details). Provide one improvement suggestion.\n" +
            "2. Give advice based on the user's provided emotion, event, thoughts, amount, and spending details (or saving details). Also, explain why you recommend each suggestion.\n" +
            "3. Example: If you feel (emotion) and want to (action), how about trying this?\n" +
            "4. Use up to 50 Korean characters.\n" +
            "5. Speak in a friendly tone as if talking to a friend, using the informal polite speech style (~해요) instead of the formal style (~다나까).\n" +
            "6. Include references to the latest trends, memes, or news in Korea.\n" +
            "\n" +
            "This response needs to be stored in a database, so ensure it is within VARCHAR(255).";

    @Value("${apiKey}")
    private String apiKey;

    /**
     * AI 코멘트를 요청하고, 결과 반환
     *
     * @param requestDto 요청 DTO
     * @return AI 응답 DTO
     */
    @Override
    @Transactional
    public AiResponseDto requestAiComment(AiRequestDto requestDto) {
        log.debug("요청 시간: {}", System.currentTimeMillis());

        List<Message> messages = prepareMessages(requestDto);
        CommentRequest commentRequest = createCommentRequest(messages);
        String aiComment = getAiComment(commentRequest);
        saveAiCommentToArticle(requestDto.getArticleId(), aiComment);

        log.debug("응답 시간: {}", System.currentTimeMillis());
        return AiResponseDto.from(aiComment, modelMapper);
    }

    /**
     * CommentRequest 생성
     *
     * @param messages 메시지 리스트
     * @return CommentRequest 객체
     */
    private CommentRequest createCommentRequest(List<Message> messages) {
        return CommentRequest.builder()
                .model(AI_MODEL)
                .messages(messages)
                .build();
    }

    /**
     * 메시지 리스트 준비
     *
     * @param requestDto 요청 DTO
     * @return 메시지 리스트
     */
    private List<Message> prepareMessages(AiRequestDto requestDto) {
        Message message1 = Message.builder()
                .role(AI_ROLE)
                .content(MESSAGE_TO_AI)
                .build();
        Message message2 = Message.builder()
                .role(USER_ROLE)
                .content(requestDto.toString())
                .build();
        return Arrays.asList(message1, message2);
    }

    /**
     * AI 코멘트를 요청하고, 결과 반환
     *
     * @param commentRequest 요청 객체
     * @return AI 코멘트
     */
    @CircuitBreaker(name = "openAiClient", fallbackMethod = "fallbackGetAiComment")
    @Retry(name = "openAiClient")
    private String getAiComment(CommentRequest commentRequest) {
        CommentResponse response = openAiClient.getAiComment(apiKey, commentRequest);
        log.debug("API Response: {}", response);

        String aiComment = response
                .getChoices()
                .stream()
                .findFirst()
                .map(choice -> choice.getMessage().getContent())
                .orElseThrow(() -> new EmptyCommentException("fail to get ai comment"));
        log.debug("AI 한마디를 성공적으로 요청했습니다.");
        return aiComment;
    }

    private String fallbackGetAiComment(Throwable t) {
        log.error("Fallback method for getAiComment due to : {}", t.getMessage(), t);
        throw new BusinessException(ErrorCode.AI_NETWORK.name(), ErrorCode.AI_NETWORK);
    }

    /**
     * AI 코멘트를 Article에 저장
     *
     * @param articleId Article ID
     * @param aiComment AI 코멘트
     */
    @Transactional
    protected void saveAiCommentToArticle(Long articleId, String aiComment) {
        Article article = articleService.findArticleById(articleId);
        article.addAiComment(aiComment);
        log.debug("AI 한마디가 저장되었습니다.");
    }
}
