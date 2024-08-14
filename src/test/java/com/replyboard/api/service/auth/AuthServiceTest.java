package com.replyboard.api.service.auth;

import com.replyboard.IntegrationTestSupport;
import com.replyboard.api.controller.auth.request.SignupRequest;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.exception.DuplicatedMemberException;
import com.replyboard.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @BeforeEach
//    public void tearDown() {
//        memberRepository.deleteAllInBatch();
//    }

    @DisplayName("회원 가입 성공")
    @Test
    void signup() {
        // given
        SignupRequest request = getSignupRequest("pps8853@gmail.com", "1234");

        // when
        authService.signup(request.toServiceRequest());

        // then
        Member findMember = memberRepository.findAll().get(0);
        assertThat(findMember).isNotNull();
        assertThat(findMember.getEmail()).isEqualTo("pps8853@gmail.com");
        assertThat(passwordEncoder.matches("1234", findMember.getPassword())).isTrue();
    }

    @DisplayName("회원 가입 성공")
    @Test
    void signupDuplicatedMember() {
        // given
        Member member = Member.builder().email("pps8853@gmail.com").password(passwordEncoder.encode("123456")).name("루디").build();
        memberRepository.save(member);

        SignupRequest request = getSignupRequest("pps8853@gmail.com", "1234");

        // when
        assertThatThrownBy(() -> authService.signup(request.toServiceRequest()))
                .isInstanceOf(DuplicatedMemberException.class)
                .hasMessage("중복된 회원이 존재합니다.");
    }

    @DisplayName("회원 가입 시 이메일은 필수다.")
    @Test
    void signupWithoutEmail() {
        // given
        SignupRequest request = getSignupRequest(null, "1234");

        // when
        assertThatThrownBy(() -> authService.signup(request.toServiceRequest()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("이메일을 입력해주세요.");
    }

    @DisplayName("회원 가입 시 이메일의 형식이 잘못되면 예외가 발생한다.")
    @Test
    void signupInvalidEmailPattern() {
        // given
        SignupRequest request = getSignupRequest("pps8853@gmailcom", "1234");

        // when
        assertThatThrownBy(() -> authService.signup(request.toServiceRequest()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("이메일 형식에 맞춰 입력해주세요.");
    }

    @DisplayName("회원 가입 시 비밀번호는 필수다")
    @Test
    void signupWithoutPassword() {
        // given
        SignupRequest request = getSignupRequest("pps8853@gmail.com", null);

        // when
        assertThatThrownBy(() -> authService.signup(request.toServiceRequest()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("비밀번호를 입력해주세요.");
    }

    private SignupRequest getSignupRequest(String email, String password) {
        return SignupRequest.builder()
                .email(email)
                .password(password)
                .name("루디")
                .build();
    }
}