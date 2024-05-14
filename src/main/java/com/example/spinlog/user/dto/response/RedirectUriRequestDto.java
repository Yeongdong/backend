package com.example.spinlog.user.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RedirectUriRequestDto {

    private final String requestedUri;

    public static RedirectUriRequestDto of(String requestedUri) {
        return new RedirectUriRequestDto(requestedUri);
    }

}
