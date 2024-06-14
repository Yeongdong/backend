package com.example.spinlog.article.service.request;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleCreateRequest {
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

    public Article toEntity(User user) {
        return Article.builder()
                .user(user)
                .content(content)
                .spendDate(LocalDateTime.parse(spendDate))
                .event(event)
                .thought(thought)
                .emotion(Emotion.valueOf(emotion))
                .satisfaction(satisfaction)
                .reason(reason)
                .improvements(improvements)
                .amount(amount)
                .registerType(RegisterType.valueOf(registerType))
                .build();
    }
}
