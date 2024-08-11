package com.replyboard.api.service.auth.request;

import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.Role;
import com.replyboard.exception.InvalidParameterException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.List;
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

        newMember.addRole(List.of(Role.ROLE_USER.name()));

        return newMember;
    }

    public void validation() {
        if (!StringUtils.hasText(email)) {
            throw new InvalidParameterException("이메일을 입력해주세요.");
        }

        if (!Pattern.matches(emailRegExp, email)) {
            throw new InvalidParameterException("이메일 형식에 맞춰 입력해주세요.");
        }

        if (!StringUtils.hasText(password)) {
            throw new InvalidParameterException("비밀번호를 입력해주세요.");
        }
    }
}
