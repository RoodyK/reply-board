package com.replyboard.api.controller.post.request;

import com.replyboard.api.service.post.request.EditPostServiceRequest;
import com.replyboard.domain.post.PostStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EditPostRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    private String title;

    private String content;

    @NotNull(message = "게시글 상태는 필수입니다.")
    private PostStatus postStatus;

    @Builder
    public EditPostRequest(Long categoryId, String title, String content, PostStatus postStatus) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.postStatus = postStatus;
    }

    public EditPostServiceRequest toServiceRequest() {
        return EditPostServiceRequest.builder()
                .categoryId(categoryId)
                .title(title)
                .content(content)
                .postStatus(postStatus)
                .build();
    }
}
