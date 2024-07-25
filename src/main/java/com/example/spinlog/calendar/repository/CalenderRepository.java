package com.example.spinlog.calendar.repository;

import com.example.spinlog.calendar.dto.DaySpend;
import com.example.spinlog.calendar.repository.dto.MonthSpendDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.spinlog.article.entity.QArticle.article;

@Repository
public class CalenderRepository {
    private final JPAQueryFactory queryFactory;
    public CalenderRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<MonthSpendDto> getMonthSpendList(Long userId, LocalDate date) {
        NumberTemplate<Integer> spendDateYear = Expressions.numberTemplate(Integer.class, "YEAR({0})", article.spendDate);
        NumberTemplate<Integer> spendDateMonth = Expressions.numberTemplate(Integer.class, "MONTH({0})", article.spendDate);
        NumberTemplate<Integer> dateYear = Expressions.numberTemplate(Integer.class, "YEAR({0})", date);
        NumberTemplate<Integer> dateMonth = Expressions.numberTemplate(Integer.class, "MONTH({0})", date);
        return queryFactory
                .select(Projections.constructor(MonthSpendDto.class,
                        article.spendDate,
                        article.amount,
                        article.registerType))
                .from(article)
                .where(
                        article.user.id.eq(userId),
                        spendDateYear.eq(dateYear).and(spendDateMonth.eq(dateMonth))
                )
                .fetch();
    }

    public List<DaySpend> getDaySpendList(Long userId, LocalDate date) {
        return queryFactory
                .select(Projections.constructor(DaySpend.class,
                        article.articleId,
                        article.registerType,
                        article.amount,
                        article.content,
                        article.satisfaction,
                        article.emotion))
                .from(article)
                .where(
                        article.user.id.eq(userId),
                        article.spendDate.between(
                                date.atStartOfDay(),
                                date.plusDays(1).atStartOfDay().minusSeconds(1L)
                        )
                )
                .fetch();
    }

}
