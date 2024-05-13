package com.example.spinlog.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
public class UpdateUserRequestDto {

    private String mbti;

    private String gender;

    private Integer budget;

    public UpdateUserRequestDto() {
    }
}
