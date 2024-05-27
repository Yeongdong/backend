package com.example.spinlog.article.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @NotNull
    private Float satisfaction;
    @NotEmpty
    private String reason;
    @NotEmpty
    private String improvements;
    @NotNull
    private Integer amount;
    @NotEmpty
    private String registerType;
}
