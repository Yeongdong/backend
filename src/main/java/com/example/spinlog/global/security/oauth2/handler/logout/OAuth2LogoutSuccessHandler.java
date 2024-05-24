package com.example.spinlog.global.security.oauth2.handler.logout;

import com.example.spinlog.global.security.session.CustomSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        request.getSession().setAttribute("redirected", true);
        response.sendRedirect("/api/authentication/logout-result");

        // TODO 세션 삭제
        String sessionId = request.getHeader("Authorization");
        CustomSessionManager.getSession(sessionId)
                .ifPresent(session -> CustomSessionManager.deleteSession(sessionId));
    }
}
