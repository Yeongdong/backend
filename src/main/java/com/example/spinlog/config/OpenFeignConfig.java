package com.example.spinlog.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(value = {"com.example.spinlog.ai", "com.example.spinlog.statistics.service"})
public class OpenFeignConfig {
}
