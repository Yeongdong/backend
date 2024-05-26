package com.example.spinlog.article.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateArticleRequestDto {
    @NotEmpty
    private String content;
    @NotEmpty
    private String event;
    @NotEmpty
    private String spendDate;
    @NotEmpty
    private String thought;
    @NotEmpty
    private String emotion;
    @NotEmpty
    private Float satisfaction;
    @NotEmpty
    private String reason;
    @NotEmpty
    private String improvements;
    @NotEmpty
    private Integer amount;
    @NotEmpty
    private String registerType;
}
