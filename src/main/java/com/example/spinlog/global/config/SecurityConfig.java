package com.example.spinlog.global.config;

import com.example.spinlog.global.config.oauth2.CustomClientRegistrationRepository;
import com.example.spinlog.global.config.oauth2.CustomOAuth2AuthorizedClientService;
import com.example.spinlog.user.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepository customClientRegistrationRepository;
    private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;
    private final JdbcTemplate jdbcTemplate;

    /*
    TODO oauth2Login() 관련
        3. 로그아웃을 한 뒤 다시 로그인을 하면 자동 로그인이 되지 않게
        4. 최초 로그인인지 판별
        5. cors 설정
     */
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
                        .clientRegistrationRepository(customClientRegistrationRepository.clientRegistrationRepository())
                        .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(
                                jdbcTemplate, customClientRegistrationRepository.clientRegistrationRepository()
                        ))
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService))

                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
