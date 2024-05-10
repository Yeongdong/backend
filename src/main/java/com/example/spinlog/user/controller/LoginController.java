package com.example.spinlog.user.controller;

import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import com.example.spinlog.user.dto.response.LoginResponseDto;
import com.example.spinlog.user.security.dto.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/login")
public class LoginController {

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<LoginResponseDto>> login(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        LoginResponseDto requestDto = LoginResponseDto.of(oAuth2User.getFirstLogin());

        ApiResponseWrapper<LoginResponseDto> response;
        if (oAuth2User.getFirstLogin()) {
            response = ResponseUtils.ok(requestDto, "회원가입에 성공했습니다.");
        } else{
            response = ResponseUtils.ok(requestDto, "로그인에 성공했습니다.");
        }

        return ResponseEntity.ok(response);
    }

}
