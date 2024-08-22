package com.replyboard.api.controller.post.request;

import com.replyboard.api.service.post.request.CreatePostServiceRequest;
import com.replyboard.domain.post.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatePostRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private final Long categoryId;

    @NotBlank(message = "제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private final String content;

    @NotNull(message = "게시글 상태는 필수입니다.")
    private PostStatus postStatus;

    @Builder
    public CreatePostRequest(Long categoryId, String title, String content, PostStatus postStatus) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.postStatus = postStatus;
    }

    public CreatePostServiceRequest toServiceRequest() {
        return CreatePostServiceRequest.builder()
                .categoryId(categoryId)
                .title(title)
                .content(content)
                .postStatus(postStatus)
                .build();
    }
}
