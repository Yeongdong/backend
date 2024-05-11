package com.example.spinlog.global.security.oauth2.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;

    private final Boolean firstLogin;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return authorities;
    }

    @Override
    public String getName() {
        return oAuth2Response.getEmail();
    }

    public String getOAuth2Provider() {
        return oAuth2Response.getProvider();
    }

    public static CustomOAuth2User of(OAuth2Response oAuth2Response, Boolean isFirstLogin) {
        return new CustomOAuth2User(oAuth2Response, isFirstLogin);
    }
}
