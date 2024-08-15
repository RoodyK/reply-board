package com.replyboard.api.controller.auth.request;

import com.replyboard.api.service.auth.request.SignupServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class SignupRequest {

    @NotBlank(message = "이메일을 입력해주세요")
    private final String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Length(min = 4, max = 20, message = "비밀번호는 4~20자 사이로 입력해주세요.")
    private final String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private final String name;

    @Builder
    public SignupRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public SignupServiceRequest toServiceRequest() {
        return SignupServiceRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }
}
