package com.example.spinlog.user.security.service;

import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import com.example.spinlog.user.security.dto.CustomOAuth2User;
import com.example.spinlog.user.security.dto.OAuth2Response;
import com.example.spinlog.user.security.dto.impl.GoogleResponse;
import com.example.spinlog.user.security.dto.impl.KakaoResponse;
import com.example.spinlog.user.security.dto.impl.NaverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    public static final String KAKAO = "kakao";
    public static final String NAVER = "naver";
    public static final String GOOGLE = "google";

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oAuth2Provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("oAuth2User: {}, oAuth2Provider: {}", oAuth2User.getAttributes(), oAuth2Provider);

        OAuth2Response response = getMatchingOAuth2Response(oAuth2Provider, oAuth2User);

        String authenticationName = response.getAuthenticationName();
        Optional<User> foundUser = userRepository.findByAuthenticationName(authenticationName);

        if (foundUser.isEmpty()) {
            log.info("User {} does not exist. Saving user into database.", authenticationName);
            userRepository.save(
                    User.builder()
                            .email(response.getEmail())
                            .authenticationName(authenticationName)
                            .build()
            );
        } else {
            log.info("User {} found", authenticationName);
            foundUser.get().changeProfile(response.getEmail());
        }

        return CustomOAuth2User.builder()
                .oAuth2Response(response)
                .build();
    }

    private static OAuth2Response getMatchingOAuth2Response(String oAuth2Provider, OAuth2User oAuth2User) {
        if (oAuth2Provider.equals(KAKAO)) {
            return KakaoResponse.of(oAuth2User.getAttributes());
        }
        if (oAuth2Provider.equals(NAVER)) {
            return NaverResponse.of(oAuth2User.getAttributes());
        }
        if (oAuth2Provider.equals(GOOGLE)) {
            return GoogleResponse.of(oAuth2User.getAttributes());
        }
        return null; //TODO 검증
    }
}
