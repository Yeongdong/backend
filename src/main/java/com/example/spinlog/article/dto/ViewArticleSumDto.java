package com.example.spinlog.article.dto;

import lombok.Data;

@Data
public class ViewArticleSumDto {
    private Long articleId;
    private String content;
    private String emotion;
    private Float satisfaction;
    private Integer amount;
    private String registerType;
}
