package com.example.spinlog.global.security.oauth2.user.impl;

import com.example.spinlog.global.security.oauth2.user.OAuth2Response;

import java.util.Map;

public class MockResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public static MockResponse of(Map<String, Object> attribute) {
        return new MockResponse(attribute);
    }

    private MockResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "mock";
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
