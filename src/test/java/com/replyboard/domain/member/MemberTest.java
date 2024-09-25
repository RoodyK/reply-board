package com.replyboard.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @DisplayName("회원의 권한을 GrantedAuthority 객체로 변환")
    @Test
    void getRolesToGrantedAuthority() {
        // given
        Member member = Member.builder()
                .name("루디")
                .email("admin@test.com")
                .password("1234")
                .build();

        Set<Role> role = Set.of(Role.ROLE_ADMIN, Role.ROLE_USER);

        member.addRole(role);

        // when
        Set<GrantedAuthority> grantedAuthority = member.getRolesToGrantedAuthority();

        // then
        assertThat(grantedAuthority).hasSize(2)
                .extracting("role")
                .containsExactlyInAnyOrder(
                        Role.ROLE_ADMIN.name(),
                        Role.ROLE_USER.name()
                );
    }

}