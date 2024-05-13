package com.example.spinlog.custom.securitycontext;

import lombok.Getter;

@Getter
public enum OAuth2Provider {

    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google");

    private final String providerName;

    OAuth2Provider(String providerName) {
        this.providerName = providerName;
    }
}
