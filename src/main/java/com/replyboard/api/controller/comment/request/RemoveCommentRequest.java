package com.replyboard.api.controller.comment.request;

import com.replyboard.api.service.comment.request.RemoveCommentServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoveCommentRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Builder
    public RemoveCommentRequest(String password) {
        this.password = password;
    }

    public RemoveCommentServiceRequest toServiceRequest() {
        return RemoveCommentServiceRequest.builder()
                .password(password)
                .build();
    }
}
