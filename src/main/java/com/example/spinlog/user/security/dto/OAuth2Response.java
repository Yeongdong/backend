package com.example.spinlog.user.security.dto;

public interface OAuth2Response {

    //제공자 (naver, google, kakao)
    String getProvider();

    //제공자에서 저장하고 있는 사용자 고유 ID
    String getProviderId();

    //사용자 이메일
    String getEmail();

    //사용자 이름
    String getName();

    String getAuthenticationName();
}
