package com.example.spinlog.article.repository;

import com.example.spinlog.article.dto.SearchCond;
import com.example.spinlog.article.dto.ViewArticleResponseDto;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.spinlog.article.entity.QArticle.article;

public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ViewArticleResponseDto> search(SearchCond cond, Pageable pageable) {
        // 게시글 가져오는 쿼리
        List<ViewArticleResponseDto> content = queryFactory
                .select(Projections.constructor(ViewArticleResponseDto.class))
                .from(article)
                .where(
                        registerTypeEq(cond.getRegisterType()),
                        emotionEq(cond.getEmotion()),
                        satisfactionEq(cond.getSatisfaction())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리
        JPAQuery<Long> count = queryFactory
                .select(article.count())
                .from(article)
                .where(
                        registerTypeEq(cond.getRegisterType()),
                        emotionEq(cond.getEmotion()),
                        satisfactionEq(cond.getSatisfaction())
                );
        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanBuilder registerTypeEq(String registerType) {
        if (StringUtils.hasText(registerType)) {
            return new BooleanBuilder(article.registerType.eq(RegisterType.valueOf(registerType)));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder emotionEq(String emotion) {
        if (StringUtils.hasText(emotion)) {
            return new BooleanBuilder(article.emotion.eq(Emotion.valueOf(emotion)));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder satisfactionEq(Float satisfaction) {
        if (satisfaction != null) {
            return new BooleanBuilder(article.satisfaction.eq(satisfaction));
        }
        return new BooleanBuilder();
    }
}
