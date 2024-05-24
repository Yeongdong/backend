package com.example.spinlog.global.security.session;

import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.global.security.oauth2.user.OAuth2Response;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String sessionId = request.getHeader("authorization");
        Optional<CustomSession> session = CustomSessionManager.getSession(sessionId);

        log.info("sessions: "+ CustomSessionManager.sessions);

        if(session.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        String authenticationName = session.get().getAuthenticationName();
        Optional<User> optionalUser = userRepository.findByAuthenticationName(authenticationName);

        if(optionalUser.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        User user = optionalUser.get();
        authenticate(user);
        filterChain.doFilter(request, response);
    }

    private void authenticate(User user) {
        OAuth2Response oAuth2Response = getOAuth2Response(user);
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

    private OAuth2Response getOAuth2Response(User user) {
        return new CustomOAuth2Response(user.getEmail(), user.getAuthenticationName());
    }
}
