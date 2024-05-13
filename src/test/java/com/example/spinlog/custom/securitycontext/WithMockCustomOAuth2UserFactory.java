package com.example.spinlog.custom.securitycontext;

import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.global.security.oauth2.user.OAuth2Response;
import com.example.spinlog.global.security.oauth2.user.impl.GoogleResponse;
import com.example.spinlog.global.security.oauth2.user.impl.KakaoResponse;
import com.example.spinlog.global.security.oauth2.user.impl.NaverResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashMap;
import java.util.Map;

import static com.example.spinlog.custom.securitycontext.OAuth2Provider.KAKAO;
import static com.example.spinlog.custom.securitycontext.OAuth2Provider.NAVER;

public class WithMockCustomOAuth2UserFactory implements WithSecurityContextFactory<WithMockCustomOAuth2User> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomOAuth2User customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        OAuth2Response oAuth2Response;
        Map<String, Object> attribute = new HashMap<>();

        if (customUser.provider() == KAKAO) {
            attribute.put("id", customUser.providerMemberId());
            attribute.put("kakao_account", Map.of("email", customUser.email()));

            oAuth2Response = KakaoResponse.of(attribute);
        } else if (customUser.provider() == NAVER) {
            attribute.put("response", Map.of(
                    "id", customUser.providerMemberId(),
                    "email", customUser.email()
                    ));

            oAuth2Response = NaverResponse.of(attribute);
        } else { //customUser.provider() == GOOGLE
            attribute.put("sub", customUser.providerMemberId());
            attribute.put("email", customUser.email());

            oAuth2Response = GoogleResponse.of(attribute);
        }

        CustomOAuth2User principal = CustomOAuth2User.builder()
                .oAuth2Response(oAuth2Response)
                .firstLogin(customUser.isFirstLogin())
                .build();
        Authentication authentication = new OAuth2AuthenticationToken(
                principal, principal.getAuthorities(), principal.getOAuth2Provider()
        );
        context.setAuthentication(authentication);

        return context;
    }
}
