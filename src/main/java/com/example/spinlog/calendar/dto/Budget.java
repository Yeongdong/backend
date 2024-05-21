package com.example.spinlog.calendar.dto;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.user.entity.BudgetEntity;
import com.example.spinlog.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Budget {
    private Integer monthBudget;
    private Integer monthSpend;
    private Integer monthSave;

    public static Budget of(User user, LocalDate localDate) {
        BudgetEntity budget = user.getBudgetOf(localDate);

        Integer monthBudget = (budget != null) ? budget.getBudget() : 0;
        return new Budget(monthBudget, getMonthSpend(user.getArticles()), getMonthSave(user.getArticles()));
    }

    private static Integer getMonthSpend(List<Article> articles) {
        return articles.stream()
                .filter(article -> article.getRegisterType() == RegisterType.SPEND)
                .mapToInt(Article::getAmount)
                .sum();
    }

    private static Integer getMonthSave(List<Article> articles) {
        return articles.stream()
                .filter(article -> article.getRegisterType() == RegisterType.SAVE)
                .mapToInt(Article::getAmount)
                .sum();
    }
}
