package com.example.spinlog.article.dto;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteArticleRequestDto {

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

    /**
     * DTO를 Article 엔티티로 변환하는 메서드
     *
     * @return 변환된 Article 엔티티
     */
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
