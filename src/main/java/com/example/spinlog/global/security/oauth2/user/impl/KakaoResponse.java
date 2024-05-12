package com.example.spinlog.global.security.oauth2.user.impl;

import com.example.spinlog.global.security.oauth2.user.OAuth2Response;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final String providerId;
    private final Map<String, Object> attribute;

    public static KakaoResponse of(Map<String, Object> attribute) {
        return new KakaoResponse(attribute);
    }

    private KakaoResponse(final Map<String, Object> attribute) {
        this.providerId = String.valueOf(attribute.get("id"));
        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getAuthenticationName() {
        return getProvider() + "_" + getProviderId();
    }
}
