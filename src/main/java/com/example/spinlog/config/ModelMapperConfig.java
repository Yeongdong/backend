package com.example.spinlog.config;

import com.example.spinlog.ai.dto.AiResponseDto;
import com.example.spinlog.article.dto.WriteArticleResponseDto;
import com.example.spinlog.article.entity.Article;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Enum -> String 컨버터
        Converter<Enum, String> enumToStringConverter = context -> {
            Enum source = context.getSource();
            return source != null ? source.name() : null;
        };

        // String -> AiResponseDto 컨버터
        Converter<String, AiResponseDto> stringToAiResponseDtoConverter = context -> {
            String aiComment = context.getSource();
            return AiResponseDto.builder().content(aiComment).build();
        };

        // Article -> WriteArticleResponseDto 매핑 설정
        modelMapper.createTypeMap(Article.class, WriteArticleResponseDto.class)
                .addMappings(mapping -> {
                    mapping.using(enumToStringConverter)
                            .map(Article::getEmotion, WriteArticleResponseDto::setEmotion);
                    mapping.using(enumToStringConverter)
                            .map(Article::getRegisterType, WriteArticleResponseDto::setRegisterType);
                });

        // String -> AiResponseDto 매핑 설정
        modelMapper.createTypeMap(String.class, AiResponseDto.class)
                .setConverter(stringToAiResponseDtoConverter);

        return modelMapper;
    }
}
