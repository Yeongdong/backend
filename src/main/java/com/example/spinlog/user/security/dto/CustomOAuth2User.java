package com.example.spinlog.user.security.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;

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

    public String getUsername() {
        return oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
    }
}
