package com.example.spinlog.global.security.oauth2.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_POST;
import static org.springframework.security.oauth2.core.oidc.IdTokenClaimNames.SUB;

@Component
public class SocialClientRegistration {

    @Value("${base-url}") private String baseUrl;

    @Value("${client-id.kakao}") private String kakaoClientId;
    @Value("${client-secret.kakao}") private String kakaoClientSecret;
    
    @Value("${client-id.naver}") private String naverClientId;
    @Value("${client-secret.naver}") private String naverClientSecret;

    @Value("${client-id.google}") private String googleClientId;
    @Value("${client-secret.google}") private String googleClientSecret;

    @Value("${client-id.mock}") private String mockClientId;
    @Value("${client-secret.mock}") private String mockClientSecret;
    @Value("${private-mock-server-url}") private String privateMockServerUrl;
    @Value("${public-mock-server-url}") private String publicMockServerUrl;

    public ClientRegistration kakaoClientRegistration() {

        return ClientRegistration.withRegistrationId("kakao")
                .clientId(kakaoClientId)
                .clientSecret(kakaoClientSecret)
                .redirectUri(baseUrl + "/login/oauth2/code/kakao")
                .clientAuthenticationMethod(CLIENT_SECRET_POST)
                .authorizationGrantType(AUTHORIZATION_CODE)
                .scope("account_email")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("kakao_account")
                .build();
    }

    public ClientRegistration naverClientRegistration() {

        return ClientRegistration.withRegistrationId("naver")
                .clientId(naverClientId)
                .clientSecret(naverClientSecret)
                .redirectUri(baseUrl + "/login/oauth2/code/naver")
                .authorizationGrantType(AUTHORIZATION_CODE)
                .scope("email")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .build();
    }

    public ClientRegistration googleClientRegistration() {

        return ClientRegistration.withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .redirectUri(baseUrl + "/login/oauth2/code/google")
                .authorizationGrantType(AUTHORIZATION_CODE)
                .scope("email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .issuerUri("https://accounts.google.com")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(SUB)
                .build();
    }

    public ClientRegistration mockClientRegistration() {

        return ClientRegistration.withRegistrationId("mock")
                .clientId(mockClientId)
                .clientSecret(mockClientSecret)
                .redirectUri(baseUrl + "/login/oauth2/code/mock")
                .authorizationGrantType(AUTHORIZATION_CODE)
                .scope("email")
                .authorizationUri(publicMockServerUrl + "/oauth2/authorize")
                .tokenUri(privateMockServerUrl + "/oauth2/token")
                .userInfoUri(privateMockServerUrl + "/oauth2/userinfo")
                .userNameAttributeName(SUB)
                .build();
    }


}
