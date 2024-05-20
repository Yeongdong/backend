package com.example.spinlog.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateArticleRequestDto {
    private String content;
    private String event;
    private String spendDate;
    private String thought;
    private String emotion;
    private Float satisfaction;
    private String reason;
    private String improvements;
    private Integer amount;
    private String registerType;
}
