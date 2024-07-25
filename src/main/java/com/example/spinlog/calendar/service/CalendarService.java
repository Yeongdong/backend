package com.example.spinlog.calendar.service;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.calendar.dto.*;
import com.example.spinlog.calendar.repository.CalenderRepository;
import com.example.spinlog.calendar.repository.dto.MonthSpendDto;
import com.example.spinlog.global.error.exception.user.UserNotFoundException;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import com.example.spinlog.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final UserRepository userRepository;
    private final CalenderRepository calenderRepository;

    public TotalCalendarResponseDto requestTotal(String userName, String selectDate) {
        User user = getUser(userName);

        LocalDate parsedDate = DateUtils.parseStringToDate(selectDate);

        BudgetDto budgetDto = BudgetDto.of(user, parsedDate);

        List<MonthSpendDto> dtos = calenderRepository.getMonthSpendList(user.getId(), parsedDate);
        List<MonthSpend> monthSpendList = createMonthSpendList(dtos);

        List<DaySpend> daySpendList = calenderRepository.getDaySpendList(user.getId(), parsedDate);

        return TotalCalendarResponseDto.builder()
                .budgetDto(budgetDto)
                .monthSpendList(monthSpendList)
                .daySpendList(daySpendList)
                .build();
    }

    public DailyCalendarResponseDto requestDaily(String userName, String selectDate) {
        User user = getUser(userName);
        LocalDate parsedDate = DateUtils.parseStringToDate(selectDate);

        List<DaySpend> daySpendList = calenderRepository.getDaySpendList(user.getId(), parsedDate);

        return DailyCalendarResponseDto.builder()
                .daySpendList(daySpendList)
                .build();
    }

    private User getUser(String userName) {
        return userRepository.findByAuthenticationName(userName).stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(userName));
    }

    private List<Article> getArticlesFromUser(String userName) {
        User user = getUser(userName);
        return user.getArticles();
    }

    private List<MonthSpend> createMonthSpendList(List<MonthSpendDto> articles) {
        return articles.stream()
                .collect(Collectors.groupingBy(article -> article.getSpendDate().toLocalDate()))
                .entrySet()
                .stream()
                .map(entry -> createMonthSpendsFromEntry(entry.getKey(), entry.getValue()))
                .toList();
    }

    private boolean isSameMonth(LocalDate parsedDate, LocalDateTime date) {
        return date.getMonth() == parsedDate.getMonth();
    }

    private MonthSpend createMonthSpendsFromEntry(LocalDate date, List<MonthSpendDto> articlesOnDate) {
        int totalDaySpend = calculateTotalAmountByType(articlesOnDate, RegisterType.SPEND);
        int totalDaySave = calculateTotalAmountByType(articlesOnDate, RegisterType.SAVE);

        return MonthSpend.builder()
                .date(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .daySpend(totalDaySpend)
                .daySave(totalDaySave)
                .build();
    }

    private DaySpend mapToDaySpend(Article article) {
        return DaySpend.builder()
                .articleId(article.getArticleId())
                .registerType(article.getRegisterType())
                .amount(article.getAmount())
                .content(article.getContent())
                .satisfaction(article.getSatisfaction())
                .emotion(article.getEmotion())
                .build();
    }

    private int calculateTotalAmountByType(List<MonthSpendDto> articles, RegisterType registerType) {
        return articles.stream()
                .filter(article -> article.getRegisterType() == registerType)
                .mapToInt(MonthSpendDto::getAmount)
                .sum();
    }
}
