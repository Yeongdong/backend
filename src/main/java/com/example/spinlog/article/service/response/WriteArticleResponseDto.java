package com.example.spinlog.article.service.response;

import com.example.spinlog.article.entity.Article;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WriteArticleResponseDto {
    private Long articleId;

    public static WriteArticleResponseDto from(Article saveArticle) {
        return WriteArticleResponseDto.builder()
                .articleId(saveArticle.getArticleId())
                .build();
    }
}
