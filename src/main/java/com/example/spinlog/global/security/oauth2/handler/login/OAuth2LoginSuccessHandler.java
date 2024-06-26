package com.example.spinlog.global.security.oauth2.handler.login;

import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.global.security.session.CustomSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final CustomSessionManager customSessionManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //response.sendRedirect("/api/authentication/login-result");
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        Boolean isFirstLogin = principal.getFirstLogin();

        String authenticationName = principal.getOAuth2Response().getAuthenticationName();
        String sessionId = customSessionManager.createSession(authenticationName);

        String queryParameter = "&token=" + sessionId;

        if(isFirstLogin)
            response.sendRedirect("https://spinlog.swygbro.com/auth?isFirstLogin=true" + queryParameter);
        else
            response.sendRedirect("https://spinlog.swygbro.com/auth?isFirstLogin=false" + queryParameter);
    }
}
