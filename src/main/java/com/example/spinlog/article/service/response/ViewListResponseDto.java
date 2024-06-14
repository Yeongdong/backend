package com.example.spinlog.article.service.response;


import com.example.spinlog.article.dto.ViewArticleSumDto;
import com.example.spinlog.utils.NullDataConverter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ViewListResponseDto {
    private boolean nextPage;
    private List<ViewArticleSumDto> spendList;

    @Builder
    public ViewListResponseDto(boolean nextPage, List<ViewArticleSumDto> spendList) {
        this.nextPage = nextPage;
        this.spendList = NullDataConverter.convertList(spendList);
    }
}
