package com.replyboard.api.service.auth.request;

import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.Role;
import com.replyboard.exception.InvalidRequestException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

@Getter
public class SignupServiceRequest {

    private final String emailRegExp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final String email;
    private final String password;
    private final String name;

    @Builder
    public SignupServiceRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Member toEntity(String encodedPassword) {
        Member newMember = Member.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .build();

        newMember.addRole(Set.of(Role.ROLE_USER));

        return newMember;
    }

    public void validation() {
        if (!StringUtils.hasText(email)) {
            throw new InvalidRequestException("이메일을 입력해주세요.");
        }

        if (!Pattern.matches(emailRegExp, email)) {
            throw new InvalidRequestException("이메일 형식에 맞춰 입력해주세요.");
        }

        // 원래는 정규식 등으로 패턴을 검사해야하지만 간단히 구현
        if (!StringUtils.hasText(password)) {
            throw new InvalidRequestException("비밀번호를 입력해주세요.");
        }
    }
}
