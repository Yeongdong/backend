package com.example.spinlog.global.filter;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class FilterConfig {

    @Value("${temporary.auth.header}")
    private String temporaryAuthHeader;

    /**
     * minimum spring security filter's order is 100
     * (org.springframework.security.config.annotation.web.builders.FilterOrderRegistration)
     * if custom filter's order is less than 100, it will be executed before spring security filter
     * */

    @Bean
    public FilterRegistrationBean<CustomExceptionHandlingFilter> customExceptionHandlingFilterRegistrationBean() {
        FilterRegistrationBean<CustomExceptionHandlingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CustomExceptionHandlingFilter());
        registrationBean.setOrder(-112);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HttpMessageLoggingFilter> httpMessageLoggingFilterRegistrationBean() {
        FilterRegistrationBean<HttpMessageLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        HttpMessageLoggingFilter filter = new HttpMessageLoggingFilter(temporaryAuthHeader);
        filter.init();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(-111);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

}
