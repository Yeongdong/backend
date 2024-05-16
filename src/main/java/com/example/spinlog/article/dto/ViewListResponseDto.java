package com.example.spinlog.article.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ViewListResponseDto {
    private boolean nextPage;
    private List<ViewArticleSumDto> spendList;
}
