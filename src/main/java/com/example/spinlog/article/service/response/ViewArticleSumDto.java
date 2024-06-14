package com.example.spinlog.article.service.response;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ViewArticleSumDto {
    private Long articleId;
    private String content;
    private Emotion emotion;
    private Float satisfaction;
    private Integer amount;
    private RegisterType registerType;
}
