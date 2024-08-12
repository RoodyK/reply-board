package com.replyboard.domain.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("이메일로 회원을 조회한다.")
    @Test
    void findByEmail() {
        // given
        String email = "pps8853@gmail.com";
        Member member = Member.builder()
                .email(email)
                .password("1234")
                .name("루디")
                .build();
        member.addRole(Set.of(Role.ROLE_USER));
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findByEmail(email).orElseThrow(RuntimeException::new);

        // then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getEmail()).isEqualTo(email);
        assertThat(findMember.getRoles()).containsExactlyInAnyOrder(Role.ROLE_USER);
        assertThat(findMember.getName()).isEqualTo("루디");
    }

    @DisplayName("이메일로 회원을 조회할때 이메일에 해당하는 회원이 없으면 예외가 발생한다.")
    @Test
    void findByEmailNotExistsEmail() {
        // given
        Member member = Member.builder()
                .email("pps8853@gmail.com")
                .password("1234")
                .name("루디")
                .build();
        member.addRole(Set.of(Role.ROLE_USER));
        memberRepository.save(member);

        // when
        assertThatThrownBy(() -> memberRepository.findByEmail("pps8853@gmail.com1").orElseThrow(RuntimeException::new))
                .isInstanceOf(RuntimeException.class);
    }

}