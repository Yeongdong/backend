package com.example.spinlog.user.repository;

import com.example.spinlog.user.entity.QBudget;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import static com.example.spinlog.user.entity.QBudget.*;

@Repository
public class BudgetRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public BudgetRepositoryCustom(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Integer getBudget(Long userId, Integer year, Integer month) {
        return queryFactory
                .select(budget1.budget)
                .from(budget1)
                .where(
                        budget1.user.id.eq(userId),
                        budget1.year.eq(year),
                        budget1.month.eq(month)
                )
                .fetchOne();
    }
}
