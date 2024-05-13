package com.example.spinlog.custom.securitycontext;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.example.spinlog.custom.securitycontext.OAuth2Provider.KAKAO;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomOAuth2UserFactory.class)
public @interface WithMockCustomOAuth2User {

    OAuth2Provider provider() default KAKAO;

    String email() default "test@example.com";

    String providerMemberId() default "123456";

    boolean isFirstLogin() default true;

}
