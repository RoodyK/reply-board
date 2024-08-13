package com.replyboard.api.service.post.request;

import com.replyboard.domain.post.PostDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatePostServiceRequest {

    private Long categoryId;
    private String title;
    private String content;

    @Builder
    public CreatePostServiceRequest(Long categoryId, String title, String content) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
    }

    public PostDto toPostDto() {
        return PostDto.builder()
                .title(title)
                .content(content)
                .build();
    }
}
