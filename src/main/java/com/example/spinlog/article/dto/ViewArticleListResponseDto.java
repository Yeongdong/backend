package com.example.spinlog.article.dto;

import com.example.spinlog.article.entity.Article;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class ViewArticleListResponseDto {
    private Long articleId;
    private String content;
    private String emotion;
    private Float satisfaction;
    private Integer amount;
    private String registerType;
}
