package com.example.spinlog.config;

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

        return modelMapper;
    }
}
