package com.example.spinlog.article.dto;

import com.example.spinlog.article.entity.Article;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class WriteArticleResponseDto {
    private Long articleId;

    public static WriteArticleResponseDto from(Article saveArticle, ModelMapper modelMapper) {
        return modelMapper.map(saveArticle, WriteArticleResponseDto.class);
    }
}
