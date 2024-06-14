package com.example.spinlog.article.service.response;


import com.example.spinlog.utils.NullDataConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewListResponseDto {
    private boolean nextPage;
    private List<ViewArticleSumDto> spendList;

    @Builder
    private ViewListResponseDto(boolean nextPage, List<ViewArticleSumDto> spendList) {
        this.nextPage = nextPage;
        this.spendList = NullDataConverter.convertList(spendList);
    }
}
