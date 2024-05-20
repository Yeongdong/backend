package com.example.spinlog.article.dto;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
