package com.replyboard.api.service.post.request;

import com.replyboard.domain.post.PostDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatePostServiceRequest {

    private String title;
    private String content;

    @Builder
    public CreatePostServiceRequest(String title, String content) {
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
