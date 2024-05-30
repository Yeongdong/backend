package com.example.spinlog.article.dto;

import jakarta.annotation.Nullable;
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
    private String spendDate;

    @Nullable
    private String event;

    @Nullable
    private String thought;

    @NotEmpty
    private String emotion;

    @NotNull
    private Float satisfaction;

    @Nullable
    private String reason;

    @Nullable
    private String improvements;

    @NotNull
    private Integer amount;

    @NotEmpty
    private String registerType;
}
