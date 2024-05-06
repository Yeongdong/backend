package com.example.spinlog.article.dto;

import com.example.spinlog.article.entity.Article;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class WriteArticleResponseDto {
    private String content;
    private String event;
    private String thought;
    private String emotion;
    private String result;
    private Float satisfaction;
    private String reason;
    private String improvements;
    private String aiComment;
    private Integer amount;
    private String registerType;

    public static WriteArticleResponseDto from(Article saveArticle, ModelMapper modelMapper) {
        return modelMapper.map(saveArticle, WriteArticleResponseDto.class);
    }
}
