package com.example.spinlog.user.controller;

import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.user.dto.response.LoginResponseDto;
import com.example.spinlog.user.dto.response.RedirectUriRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    @GetMapping("/login-result")
    public ApiResponseWrapper<LoginResponseDto> login(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        LoginResponseDto requestDto = LoginResponseDto.of(oAuth2User.getFirstLogin());

        if (oAuth2User.getFirstLogin()) {
            return ResponseUtils.ok(requestDto, "회원가입에 성공했습니다.");
        }

        return ResponseUtils.ok(requestDto, "로그인에 성공했습니다.");
    }

    @GetMapping("/logout-result")
    public ApiResponseWrapper<Object> logout(HttpServletRequest request, HttpServletResponse httpResponse) {
        Boolean isRedirected = (Boolean) request.getSession().getAttribute("redirected");

        if (isRedirected != null && isRedirected) {
            request.getSession().removeAttribute("redirected");
            return ResponseUtils.ok("정상적으로 로그아웃되었습니다.");
        }

        return ResponseUtils.error("잘못된 접근입니다."); //TODO 리팩토링
    }

    @GetMapping("/not-authenticated") // 로그인 하지 않은 사용자가 접근할 때 리다이렉트되는 페이지
    public ApiResponseWrapper<Object> notAuthenticated(@RequestParam("redirectURI") String redirectURI) {
        RedirectUriRequestDto requestDto = RedirectUriRequestDto.of(redirectURI);

        return ResponseUtils.error("인증되지 않은 사용자의 요청입니다.", requestDto);
    }

}
