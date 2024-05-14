package com.example.spinlog.dashboard.service;

import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.dashboard.dto.DailyAmount;
import com.example.spinlog.dashboard.dto.DashboardResponseDto;
import com.example.spinlog.dashboard.dto.EmotionAmount;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import com.example.spinlog.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final UserRepository userRepository;

    public DashboardResponseDto requestData(String userName, String selectDate, String registerType) {
        User user = getUser(userName);
        List<Article> articles = user.getArticles();
        LocalDate parsedDate = DateUtils.parseStringToDate(selectDate);

        List<Article> filteredArticles = filterArticlesByDateAndRegisterType(articles, parsedDate, registerType);

        Float averageSatisfaction = (float) filteredArticles.stream()
                .mapToDouble(Article::getSatisfaction)
                .average()
                .orElse(0.0);

        List<EmotionAmount> emotionAmount = filteredArticles.stream()
                .collect(Collectors.groupingBy(Article::getEmotion, Collectors.summingInt(Article::getAmount)))
                .entrySet()
                .stream()
                .map(entry -> EmotionAmount.of(entry.getKey(), entry.getValue()))
                .toList();

        List<DailyAmount> dailyAmount = filteredArticles.stream()
                .collect(Collectors.groupingBy(article -> article.getSpendDate().toLocalDate(), Collectors.summingInt(Article::getAmount)))
                .entrySet()
                .stream()
                .map(entry -> DailyAmount.of(entry.getKey(), entry.getValue()))
                .toList();

        return DashboardResponseDto.builder()
                .satisfactionAverage(averageSatisfaction)
                .emotionAmountTotal(emotionAmount)
                .dailyAmount(dailyAmount)
                .build();
    }

    private User getUser(String userName) {
        return userRepository.findByAuthenticationName(userName).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(userName + "에 해당하는 회원을 찾을 수 없습니다."));
    }

    private List<Article> filterArticlesByDateAndRegisterType(List<Article> articles, LocalDate parsedDate, String registerType) {
        return articles.stream()
                .filter(article -> article.getSpendDate().getMonth() == parsedDate.getMonth()
                        && isMatchingRegisterType(article, registerType))
                .collect(Collectors.toList());
    }

    private boolean isMatchingRegisterType(Article article, String registerType) {
        return article.getRegisterType() == RegisterType.valueOf(registerType);
    }
}
