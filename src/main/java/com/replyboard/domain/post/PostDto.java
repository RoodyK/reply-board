package com.replyboard.domain.post;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDto {

    private final String title;
    private final String content;
    private PostStatus postStatus;

    @Builder
    public PostDto(String title, String content, PostStatus postStatus) {
        this.title = title;
        this.content = content;
        this.postStatus = postStatus;
    }
}
