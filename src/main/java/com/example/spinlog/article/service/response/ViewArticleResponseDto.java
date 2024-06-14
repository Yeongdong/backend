package com.example.spinlog.article.service.response;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.utils.NullDataConverter;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewArticleResponseDto {
    private String content;
    private String event;
    private String spendDate;
    private String thought;
    private String emotion;
    private Float satisfaction;
    private String reason;
    private String improvements;
    private String aiComment;
    private Integer amount;
    private String registerType;

    public static ViewArticleResponseDto from(Article viewArticle) {
        ViewArticleResponseDto responseDto = ViewArticleResponseDto.builder()
                .content(viewArticle.getContent())
                .event(viewArticle.getEvent())
                .spendDate(String.valueOf(viewArticle.getSpendDate()))
                .thought(viewArticle.getThought())
                .emotion(viewArticle.getEmotion().name())
                .satisfaction(viewArticle.getSatisfaction())
                .reason(viewArticle.getReason())
                .improvements(viewArticle.getImprovements())
                .aiComment(viewArticle.getAiComment())
                .amount(viewArticle.getAmount())
                .registerType(viewArticle.getRegisterType().name())
                .build();

        String nullChecked = NullDataConverter.convertString(responseDto.getAiComment());
        responseDto.setAiComment(nullChecked);

        return responseDto;
    }
}
