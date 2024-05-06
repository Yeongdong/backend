package com.example.spinlog.article.repository;

import com.example.spinlog.article.dto.SearchCond;
import com.example.spinlog.article.dto.ViewArticleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {
    Page<ViewArticleResponseDto> search(SearchCond cond, Pageable pageable);
}
