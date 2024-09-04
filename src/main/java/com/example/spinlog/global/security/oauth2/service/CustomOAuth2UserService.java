package com.example.spinlog.global.security.oauth2.service;

import com.example.spinlog.global.security.oauth2.user.impl.MockResponse;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.global.security.oauth2.user.OAuth2Response;
import com.example.spinlog.global.security.oauth2.user.impl.GoogleResponse;
import com.example.spinlog.global.security.oauth2.user.impl.KakaoResponse;
import com.example.spinlog.global.security.oauth2.user.impl.NaverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_CLIENT;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    public static final String KAKAO = "kakao";
    public static final String NAVER = "naver";
    public static final String GOOGLE = "google";
    private static final String MOCK = "mock";

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oAuth2Provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("oAuth2User: {}, oAuth2Provider: {}", oAuth2User.getAttributes(), oAuth2Provider);

        OAuth2Response response = getMatchingOAuth2Response(oAuth2Provider, oAuth2User);

        String authenticationName = response.getAuthenticationName();
        Optional<User> foundUser = userRepository.findByAuthenticationName(authenticationName);

        if (foundUser.isPresent()) { //회원가입이 되어있는 경우 (이미 로그인한 적이 있는 경우)
            log.info("User {} found", response.getProvider() + "-" + response.getEmail());

            foundUser.get().change(response.getEmail());
            return CustomOAuth2User.of(response, false);
        }
        //회원가입이 되어있지 않은 경우 (처음 로그인한 경우)
        log.info("User {} does not exist. Saving user into database.", response.getProvider() + "-" + response.getEmail());

        userRepository.save(
                User.builder()
                        .email(response.getEmail())
                        .authenticationName(response.getAuthenticationName())
                        .build()
        );

        return CustomOAuth2User.of(response, true);
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
        if(oAuth2Provider.equals(MOCK)) {
            return MockResponse.of(oAuth2User.getAttributes());
        }

        throw new OAuth2AuthenticationException(new OAuth2Error(INVALID_CLIENT), "Unsupported provider: " + oAuth2Provider);
    }
}
