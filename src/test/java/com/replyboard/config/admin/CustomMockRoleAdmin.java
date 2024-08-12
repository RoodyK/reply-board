package com.replyboard.config.admin;

import com.replyboard.domain.member.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithSecurityContext(factory = AdminMockSecurityContext.class) // 시큐리티 테스트용 SecurityContext 생성을 명시
public @interface CustomMockRoleAdmin {

    String email() default "admin3@gmail.com";

    String password() default "1234";

    String name() default "루디";

    Role role() default Role.ROLE_ADMIN;
}
