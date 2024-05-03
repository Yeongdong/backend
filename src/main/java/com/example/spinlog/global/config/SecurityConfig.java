package com.example.spinlog.global.config;

import com.example.spinlog.user.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .disable()
                )
                .formLogin(form -> form
                        .disable()
                )
                .httpBasic(basic -> basic
                        .disable()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
