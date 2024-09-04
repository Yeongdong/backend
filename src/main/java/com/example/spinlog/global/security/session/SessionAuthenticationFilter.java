package com.example.spinlog.global.security.session;

import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.global.security.oauth2.user.OAuth2Response;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {
    private final CustomSessionManager customSessionManager;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String sessionId = request.getHeader("authorization");

        if(sessionId == null){
            filterChain.doFilter(request, response);
            return;
        }

        Optional<CustomSession> session = customSessionManager.getSession(sessionId);

        if(session.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        String authenticationName = session.get().getAuthenticationName();
        String email = session.get().getEmail();
        authenticate(authenticationName, email);
        filterChain.doFilter(request, response);
    }

    private void authenticate(String authenticationName, String email) {
        OAuth2Response oAuth2Response = new CustomOAuth2Response(email, authenticationName);
        CustomOAuth2User principal = CustomOAuth2User.builder()
                .oAuth2Response(oAuth2Response)
                .firstLogin(false)
                .build();
        Authentication authentication = new OAuth2AuthenticationToken(
                principal, principal.getAuthorities(), principal.getOAuth2Provider()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
