package com.replyboard.api.controller.post.request;

import com.replyboard.api.service.post.request.CreatePostServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatePostRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private final String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private final String content;

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    @Builder
    public CreatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public CreatePostServiceRequest toServiceRequest() {
        return CreatePostServiceRequest.builder()
                .title(title)
                .content(content)
                .build();
    }
}
