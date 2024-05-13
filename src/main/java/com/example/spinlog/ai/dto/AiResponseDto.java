package com.example.spinlog.ai.dto;

import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {
    private String content;

    public static AiResponseDto from(String aiComment, ModelMapper modelMapper) {
        return modelMapper.map(aiComment, AiResponseDto.class);
    }
}
