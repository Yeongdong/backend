package com.example.spinlog.calendar.dto;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.calendar.repository.dto.MonthSpendDto;
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

    public static BudgetDto of(User user, LocalDate localDate, List<MonthSpendDto> dtos) {
        Budget budget = user.getBudgetOf(localDate);

        Integer monthBudget = (budget != null) ? budget.getBudget() : 0;
        return new BudgetDto(monthBudget, getMonthSpend(dtos), getMonthSave(dtos));
    }

    private static Integer getMonthSpend(List<MonthSpendDto> dtos) {
        return dtos.stream()
                .filter(article -> article.getRegisterType() == RegisterType.SPEND)
                .mapToInt(MonthSpendDto::getAmount)
                .sum();
    }

    private static Integer getMonthSave(List<MonthSpendDto> dtos) {
        return dtos.stream()
                .filter(article -> article.getRegisterType() == RegisterType.SAVE)
                .mapToInt(MonthSpendDto::getAmount)
                .sum();
    }
}
