package com.replyboard.config.admin;

import com.replyboard.api.dto.MemberDto;
import com.replyboard.api.security.auth.CustomUserDetails;
import com.replyboard.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Set;

@RequiredArgsConstructor
public class AdminMockSecurityContext implements WithSecurityContextFactory<CustomMockRoleAdmin> {

//    private final PasswordEncoder passwordEncoder;
//    private final MemberRepository memberRepository;

    @Override
    public SecurityContext createSecurityContext(CustomMockRoleAdmin annotation) {
//        Member member = Member.builder()
//                .email(annotation.email())
//                .password(passwordEncoder.encode(annotation.password()))
//                .name(annotation.name())
//                .build();
//        member.addRole(Set.of(annotation.role()));
//
//        memberRepository.save(member);

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
