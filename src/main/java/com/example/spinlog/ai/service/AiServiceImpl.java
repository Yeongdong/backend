package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.*;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:apikey.yml")
public class AiServiceImpl implements AiService {

    private final OpenAiClient openAiClient;
    private final ArticleService articleService;
    private final ModelMapper modelMapper;

    private final static String AI_MODEL = "gpt-3.5-turbo";
    private final static String AI_ROLE = "system";
    private final static String USER_ROLE = "user";
    private final static String MESSAGE_TO_AI = "You are collecting emotional and consumption data from users in their 20s and 30s. Connect emotions and consumption and provide users with consumption-related advice in one sentence in Korean.";

    @Value("${apiKey}")
    private String apiKey;

    /**
     * AI 코멘트를 요청하고, 결과 반횐.
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
    private String getAiComment(CommentRequest commentRequest) {
        String aiComment = openAiClient
                .getAiComment(apiKey, commentRequest)
                .getChoices()
                .stream()
                .findFirst()
                .map(choice -> choice.getMessage().getContent())
                .orElseThrow(() -> new IllegalStateException("AI 한마디 가져오기 실패"));
        log.debug("AI 한마디를 성공적으로 요청했습니다.");
        return aiComment;
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
