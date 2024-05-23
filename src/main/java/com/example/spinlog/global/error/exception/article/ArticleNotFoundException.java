package com.example.spinlog.global.error.exception.article;

import com.example.spinlog.global.error.exception.ErrorCode;
import com.example.spinlog.global.error.exception.NotFoundException;

public class ArticleNotFoundException extends NotFoundException {
    public ArticleNotFoundException(final String id) {
        super(id, ErrorCode.ARTICLE_NOT_FOUND);
    }
}
