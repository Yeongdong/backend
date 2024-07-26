package com.example.spinlog.calendar.service;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.calendar.dto.*;
import com.example.spinlog.calendar.repository.CalenderRepository;
import com.example.spinlog.calendar.repository.dto.CalenderDto;
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

        List<CalenderDto> dtos = calenderRepository.getMonthSpendList2(user.getId(), parsedDate);

        BudgetDto budgetDto = BudgetDto.of(user, parsedDate, dtos);
        List<MonthSpend> monthSpendList = createMonthSpendList(dtos);

        List<DaySpend> daySpendList = dtos.stream()
                .filter(dto -> dto.getSpendDate().toLocalDate().equals(parsedDate))
                .map(DaySpend::of)
                .toList();

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
        return userRepository.findByAuthenticationName(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));
    }

    private List<MonthSpend> createMonthSpendList(List<CalenderDto> dtos) {
        return dtos.stream()
                .collect(Collectors.groupingBy(article -> article.getSpendDate().toLocalDate()))
                .entrySet()
                .stream()
                .map(entry -> createMonthSpendsFromEntry(entry.getKey(), entry.getValue()))
                .toList();
    }

    private MonthSpend createMonthSpendsFromEntry(LocalDate date, List<CalenderDto> articlesOnDate) {
        int totalDaySpend = calculateTotalAmountByType(articlesOnDate, RegisterType.SPEND);
        int totalDaySave = calculateTotalAmountByType(articlesOnDate, RegisterType.SAVE);

        return MonthSpend.builder()
                .date(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .daySpend(totalDaySpend)
                .daySave(totalDaySave)
                .build();
    }

    private int calculateTotalAmountByType(List<CalenderDto> articles, RegisterType registerType) {
        return articles.stream()
                .filter(article -> article.getRegisterType() == registerType)
                .mapToInt(CalenderDto::getAmount)
                .sum();
    }
}
