package com.example.spinlog.global.security.oauth2.user.impl;

import com.example.spinlog.global.security.oauth2.user.OAuth2Response;

import java.util.Map;

public class GoogleResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public static GoogleResponse of(Map<String, Object> attribute) {
        return new GoogleResponse(attribute);
    }

    private GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
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
