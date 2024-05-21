package com.example.spinlog.calendar.dto;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.user.entity.Budget;
import com.example.spinlog.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BudgetDto {
    private Integer monthBudget;
    private Integer monthSpend;
    private Integer monthSave;

    public static BudgetDto of(User user, LocalDate localDate) {
        Budget budget = user.getBudgetOf(localDate);

        Integer monthBudget = (budget != null) ? budget.getBudget() : 0;
        return new BudgetDto(monthBudget, getMonthSpend(user.getArticles(), localDate), getMonthSave(user.getArticles(), localDate));
    }

    private static Integer getMonthSpend(List<Article> articles, LocalDate localDate) {
        return articles.stream()
                .filter(article -> article.getRegisterType() == RegisterType.SPEND)
                .filter(article -> isSameMonth(article.getSpendDate(), localDate))
                .mapToInt(Article::getAmount)
                .sum();
    }

    private static Integer getMonthSave(List<Article> articles, LocalDate localDate) {
        return articles.stream()
                .filter(article -> article.getRegisterType() == RegisterType.SAVE)
                .filter(article -> isSameMonth(article.getSpendDate(), localDate))
                .mapToInt(Article::getAmount)
                .sum();
    }

    private static boolean isSameMonth(LocalDateTime spendDate, LocalDate localDate) {
        return spendDate.getYear() == localDate.getYear()
                && spendDate.getMonthValue() == localDate.getMonthValue();
    }
}
