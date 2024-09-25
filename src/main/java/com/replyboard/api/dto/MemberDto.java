package com.replyboard.api.dto;

import com.replyboard.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@ToString
public class MemberDto {

    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final Set<GrantedAuthority> roles;

    @Builder
    public MemberDto(Long id, String email, String password, String name, Set<GrantedAuthority> roles) {
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
                .roles(member.getRolesToGrantedAuthority())
                .build();
    }
}
