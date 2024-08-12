package com.replyboard.config.user;

import com.replyboard.api.dto.MemberDto;
import com.replyboard.api.security.auth.CustomUserDetails;
import com.replyboard.config.admin.CustomMockRoleAdmin;
import com.replyboard.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Set;

@RequiredArgsConstructor
public class UserMockSecurityContext implements WithSecurityContextFactory<CustomMockRoleUser> {

    @Override
    public SecurityContext createSecurityContext(CustomMockRoleUser annotation) {
        MemberDto memberDto = MemberDto.builder()
                .id(1L)
                .email(annotation.email())
                .password(annotation.password())
                .name(annotation.name())
                .roles(Set.of(new SimpleGrantedAuthority(annotation.role().name())))
                .build();

        CustomUserDetails details = new CustomUserDetails(memberDto);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(details, memberDto.getPassword(), memberDto.getRoles());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(token);

        return securityContext;
    }
}
