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
     * spring security filter's minimum order is -100
     * if custom filter's order is less than -100, it will be executed before spring security filter
     * */

    @Bean
    public FilterRegistrationBean<CustomExceptionHandlingFilter> customExceptionHandlingFilterRegistrationBean() {
        FilterRegistrationBean<CustomExceptionHandlingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CustomExceptionHandlingFilter());
        registrationBean.setOrder(-111);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HttpMessageLoggingFilter> httpMessageLoggingFilterRegistrationBean() {
        FilterRegistrationBean<HttpMessageLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        HttpMessageLoggingFilter filter = new HttpMessageLoggingFilter(temporaryAuthHeader);
        registrationBean.setFilter(filter);
        registrationBean.setOrder(-112);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

}
