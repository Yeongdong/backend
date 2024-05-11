package com.example.spinlog.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.example.spinlog.ai")
public class OpenFeignConfig {
}
