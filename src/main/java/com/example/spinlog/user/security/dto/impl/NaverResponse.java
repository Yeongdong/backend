package com.example.spinlog.user.security.dto.impl;

import com.example.spinlog.user.security.dto.OAuth2Response;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public static NaverResponse of(Map<String, Object> attribute) {
        return new NaverResponse(attribute);
    }

    private NaverResponse(final Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
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
