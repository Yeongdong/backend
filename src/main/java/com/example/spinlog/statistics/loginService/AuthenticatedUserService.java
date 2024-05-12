package com.example.spinlog.statistics.loginService;

import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticatedUserService {
    private final UserRepository userRepository;
    public Mbti getUserMBTI() {
        // TODO 컨트롤러 레이어로 리팩토링
        CustomOAuth2User oAuth2User = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticationName = oAuth2User.getOAuth2Response().getAuthenticationName();
        User user = userRepository
                .findByAuthenticationName(authenticationName)
                .orElseThrow(() -> new UsernameNotFoundException("can't find user"));
        return user.getMbti();
    }
}
