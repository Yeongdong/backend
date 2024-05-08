package com.example.spinlog.config;

import com.example.spinlog.article.dto.WriteArticleResponseDTO;
import com.example.spinlog.article.entity.Article;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Enum -> String으로 변환
        Converter<Enum, String> enumToStringConverter = new Converter<>() {
            @Override
            public String convert(MappingContext<Enum, String> context) {
                return context.getSource() != null ? context.getSource().name() : null;
            }
        };

        // Article -> WriteArticleResponseDTO 매핑 규칙 설정
        modelMapper.createTypeMap(Article.class, WriteArticleResponseDTO.class)
                .addMappings(mapping -> {
                    // Emotion 필드를 String으로 매핑
                    mapping.using(enumToStringConverter)
                            .map(Article::getEmotion, WriteArticleResponseDTO::setEmotion);
                    // RegisterType 필드를 String으로 매핑
                    mapping.using(enumToStringConverter)
                            .map(Article::getRegisterType, WriteArticleResponseDTO::setRegisterType);
                });

        return modelMapper;
    }
}
