package com.example.spinlog.global.security.oauth2.handler.logout;

import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LogoutHandler implements LogoutHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        authorizedClientService.removeAuthorizedClient(oAuth2User.getOAuth2Provider(), oAuth2User.getName());
    }
}
