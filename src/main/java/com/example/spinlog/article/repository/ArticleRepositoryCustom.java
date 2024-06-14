package com.example.spinlog.article.repository;

import com.example.spinlog.article.service.request.SearchCond;
import com.example.spinlog.article.service.response.ViewArticleSumDto;
import com.example.spinlog.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {
    Page<ViewArticleSumDto> search(User user, Pageable pageable, SearchCond cond);
}
