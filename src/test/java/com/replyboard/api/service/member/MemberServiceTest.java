package com.replyboard.api.service.member;

import com.replyboard.IntegrationTestSupport;
import com.replyboard.api.service.member.response.MemberResponse;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.member.Role;
import com.replyboard.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원 정보를 조회한다")
    @Test
    void getProfile() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        // when
        MemberResponse response = memberService.getProfile(member.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(member.getId());
        assertThat(response.getName()).isEqualTo("루디");
    }

    @DisplayName("회원 정보를 조회할 때 회원이 존재하지 않으면 예외가 발생한다.")
    @Test
    void getProfileNotExistsMember() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        // when
        assertThatThrownBy(() -> memberService.getProfile(member.getId() + 1))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("회원을 찾을 수 없습니다.");
    }


    private Member createMember(String name, String email) {
        Member member = Member.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode("1234"))
                .build();

        member.addRole(Set.of(Role.ROLE_ADMIN));

        return member;
    }
}