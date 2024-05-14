package com.example.spinlog.article.repository;

import com.example.spinlog.article.dto.SearchCond;
import com.example.spinlog.article.dto.ViewArticleSumDto;
import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.utils.DateUtils;
import com.example.spinlog.utils.ParameterParser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.spinlog.article.entity.QArticle.article;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ViewArticleSumDto> search(User user, Pageable pageable, SearchCond cond) {

        List<RegisterType> registerTypes = ParameterParser.parseRegisterTypes(cond.getRegisterType());
        List<Emotion> emotions = ParameterParser.parseEmotions(cond.getEmotion());
        List<Float> satisfactions = ParameterParser.parseCommaSeparatedFloat(cond.getSatisfaction());
        List<String> words = ParameterParser.decodeAndParse(cond.getWord());
        LocalDate from = DateUtils.parseStringToDate(cond.getFrom());
        LocalDate to = DateUtils.parseStringToDate(cond.getTo());

        // 게시글 가져오는 쿼리
        List<ViewArticleSumDto> content = queryFactory
                .select(Projections.constructor(ViewArticleSumDto.class,
                        article.articleId,
                        article.content,
                        article.emotion,
                        article.satisfaction,
                        article.amount,
                        article.registerType))
                .from(article)
                .where(
                        userIdEq(user),
                        registerTypeEq(registerTypes),
                        emotionEq(emotions),
                        satisfactionEq(satisfactions),
                        wordContains(words),
                        dateBetween(from, to)
                )
                .orderBy(article.spendDate.desc())
                .fetch();

        // 전체 카운트 쿼리
        JPAQuery<Long> count = queryFactory
                .select(article.count())
                .from(article)
                .where(
                        userIdEq(user),
                        registerTypeEq(registerTypes),
                        emotionEq(emotions),
                        satisfactionEq(satisfactions),
                        wordContains(words),
                        dateBetween(from, to)
                );
        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression userIdEq(User user) {
        return article.user.eq(user);
    }

    private BooleanExpression registerTypeEq(List<RegisterType> registerType) {
        return registerType.isEmpty() ? null : article.registerType.in(registerType);
    }

    private BooleanExpression emotionEq(List<Emotion> emotion) {
        return emotion.isEmpty() ? null : article.emotion.in(emotion);
    }

    private BooleanExpression satisfactionEq(List<Float> satisfaction) {
        return satisfaction.isEmpty() ? null : article.satisfaction.in(satisfaction);
    }

    private BooleanExpression wordContains(List<String> words) {
        BooleanExpression wordConditions = null;
        for (String word : words) {
            BooleanExpression condition = article.content.contains(word)
                    .or(article.event.contains(word))
                    .or(article.thought.contains(word))
                    .or(article.reason.contains(word));
            wordConditions = (wordConditions != null) ? wordConditions.and(condition) : condition;
        }
        return wordConditions;
    }

    private BooleanExpression dateBetween(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            return null;
        }
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay().minusNanos(1);
        return article.spendDate.between(fromDateTime, toDateTime);
    }
}
