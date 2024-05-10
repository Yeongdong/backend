package com.example.spinlog.user.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponseDto {

    private final Boolean isFirstLogin;

    public static LoginResponseDto of(Boolean isFirstLogin) {
        return new LoginResponseDto(isFirstLogin);
    }
}
