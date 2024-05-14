package com.example.spinlog.dashboard.controller;

import com.example.spinlog.dashboard.dto.DashboardResponseDto;
import com.example.spinlog.dashboard.service.DashboardService;
import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping()
    public ApiResponseWrapper<DashboardResponseDto> viewDashboard(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestParam String selectDate, @RequestParam String registerType) {
        String userName = oAuth2User.getOAuth2Response().getAuthenticationName();
        DashboardResponseDto dashboardResponseDto = dashboardService.requestData(userName, selectDate, registerType);
        return ResponseUtils.ok(dashboardResponseDto, "대시보드가 성공적으로 조회되었습니다.");
    }
}
