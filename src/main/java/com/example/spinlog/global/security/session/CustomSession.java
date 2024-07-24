package com.example.spinlog.global.security.session;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
public class CustomSession {
    private String authenticationName;
    private LocalDateTime createdTime;
    private LocalDateTime lastAccessedTime;
    private String email;

    public void updateLastAccessedTime() {
        this.lastAccessedTime = LocalDateTime.now();
    }
}
