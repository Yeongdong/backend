package com.example.spinlog.global.security.oauth2.handler.logout;

import com.example.spinlog.global.security.session.CustomSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {
    private final CustomSessionManager customSessionManager;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        String sessionId = request.getHeader("Authorization");
        customSessionManager.getSession(sessionId)
                .ifPresent(session -> customSessionManager.deleteSession(sessionId));
    }
}
