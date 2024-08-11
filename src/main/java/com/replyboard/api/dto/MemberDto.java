package com.replyboard.api.dto;

import com.replyboard.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@ToString
public class MemberDto {

    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final Collection<GrantedAuthority> roles;

    @Builder
    public MemberDto(Long id, String email, String password, String name, Collection<GrantedAuthority> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.roles = roles;
    }

    public static MemberDto of(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .roles(member.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .build();
    }
}
