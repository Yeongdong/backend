package com.example.spinlog.config;

import com.example.spinlog.ai.dto.AiResponseDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // String -> AiResponseDto 컨버터
        Converter<String, AiResponseDto> stringToAiResponseDtoConverter = context -> {
            String aiComment = context.getSource();
            return AiResponseDto.builder().content(aiComment).build();
        };

        // String -> AiResponseDto 매핑 설정
        modelMapper.createTypeMap(String.class, AiResponseDto.class)
                .setConverter(stringToAiResponseDtoConverter);

        return modelMapper;
    }
}
