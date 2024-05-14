package com.example.spinlog.calendar.controller;

import com.example.spinlog.calendar.dto.DailyCalendarResponseDto;
import com.example.spinlog.calendar.dto.TotalCalendarResponseDto;
import com.example.spinlog.calendar.service.CalendarService;
import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/articles/main")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    public ApiResponseWrapper<?> mainPage(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestParam String selectDate, @RequestParam String isSelectDay) {
        String userName = oAuth2User.getOAuth2Response().getAuthenticationName();

        boolean isTotalData = Boolean.parseBoolean(isSelectDay);

        if (isTotalData) {
            TotalCalendarResponseDto response = calendarService.requestTotal(userName, selectDate);
            return ResponseUtils.ok(response, "메인 페이지 전체 데이터 불러오기 성공");
        } else {
            DailyCalendarResponseDto response = calendarService.requestDaily(userName, selectDate);
            return ResponseUtils.ok(response, "메인 페이지 일별 데이터 불러오기 성공");
        }
    }
}
