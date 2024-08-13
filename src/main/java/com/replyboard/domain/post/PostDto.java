package com.replyboard.domain.post;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDto {

    private final String title;
    private final String content;

    @Builder
    public PostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
