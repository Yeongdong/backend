package com.example.spinlog.article.service.response;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewArticleSumDto {
    private Long articleId;
    private String content;
    private Emotion emotion;
    private Float satisfaction;
    private Integer amount;
    private RegisterType registerType;
}
