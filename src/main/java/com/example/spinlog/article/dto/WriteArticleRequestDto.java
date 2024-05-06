package com.example.spinlog.article.dto;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WriteArticleRequestDto {
    private String content;
    private String event;
    private String thought;
    private String emotion;
    private String result;
    private Float satisfaction;
    private String reason;
    private String improvements;
    private String aiComment;
    private Integer amount;
    private String registerType;

    /**
     * DTO를 Article 엔티티로 변환하는 메서드
     *
     * @return 변환된 Article 엔티티
     */
    public Article toEntity() {
        return Article.builder()
                .content(content)
                .event(event)
                .thought(thought)
                .emotion(Emotion.valueOf(emotion))
                .result(result)
                .satisfaction(satisfaction)
                .reason(reason)
                .improvements(improvements)
                .aiComment(aiComment)
                .amount(amount)
                .registerType(RegisterType.valueOf(registerType))
                .build();
    }
}
