package com.example.spinlog.article.service.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleUpdateRequest {

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
