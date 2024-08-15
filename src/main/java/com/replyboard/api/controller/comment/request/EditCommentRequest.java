package com.replyboard.api.controller.comment.request;

import com.replyboard.api.service.comment.request.EditCommentServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class EditCommentRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Length(min = 4, max = 16, message = "비밀번호는 4~16자 사이로 입력해주세요.")
    private String password;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;

    @Builder
    public EditCommentRequest(String password, String content) {
        this.password = password;
        this.content = content;
    }

    public EditCommentServiceRequest toServiceRequest() {
        return EditCommentServiceRequest.builder()
                .password(password)
                .content(content)
                .build();
    }
}
