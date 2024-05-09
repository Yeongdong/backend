package com.example.spinlog.article.dto;

import lombok.Data;

import java.util.List;

@Data
public class ViewArticleListResponseDto {
    private List<ViewArticleSumDto> viewArticleSumDtoList;
}
