package com.example.spinlog.article.service.response;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.utils.NullDataConverter;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class ViewArticleResponseDto {
    private String content;
    private String event;
    private String spendDate;
    private String thought;
    private String emotion;
    private Float satisfaction;
    private String reason;
    private String improvements;
    private String aiComment;
    private Integer amount;
    private String registerType;

    public static ViewArticleResponseDto from(Article viewArticle, ModelMapper modelMapper) {
        ViewArticleResponseDto responseDto = modelMapper.map(viewArticle, ViewArticleResponseDto.class);

        String nullChecked = NullDataConverter.convertString(responseDto.getAiComment());
        responseDto.setAiComment(nullChecked);

        return responseDto;
    }
}
