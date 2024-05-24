package com.example.spinlog.global.security.session;

import com.example.spinlog.global.security.oauth2.user.OAuth2Response;

public class CustomOAuth2Response implements OAuth2Response {
    private final String provider;
    private final String providerId;
    private final String email;
    private final String authenticationName;

    public CustomOAuth2Response(String email, String authenticationName) {
        this.email = email;
        this.authenticationName = authenticationName;

        String[] strings = authenticationName.split("_");
        this.provider = strings[0];
        this.providerId = strings[1];
    }

    @Override
    public String getProvider() {
        return this.provider;
    }

    @Override
    public String getProviderId() {
        return this.providerId;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getAuthenticationName() {
        return this.authenticationName;
    }
}
