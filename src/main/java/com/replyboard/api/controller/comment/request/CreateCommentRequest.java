package com.replyboard.api.controller.comment.request;

import com.replyboard.api.service.comment.request.CreateCommentServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CreateCommentRequest {

    @NotBlank(message = "작성자명을 입력해주세요.")
    private String author;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Length(min = 4, max = 16, message = "비밀번호는 4~16자 사이로 입력해주세요.")
    private String password;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;

    @Builder
    public CreateCommentRequest(String author, String password, String content) {
        this.author = author;
        this.password = password;
        this.content = content;
    }

    public CreateCommentServiceRequest toServiceRequest() {
        return CreateCommentServiceRequest.builder()
                .author(author)
                .password(password)
                .content(content)
                .build();
    }
}
