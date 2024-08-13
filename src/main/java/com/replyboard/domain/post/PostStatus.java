package com.replyboard.domain.post;

import lombok.Getter;

@Getter
public enum PostStatus {

    PUBLIC("공개"),
    PRIVATE("비공개"),
    BLOCKED("차단됨"),
    ;

    private final String description;

    PostStatus(String description) {
        this.description = description;
    }
}
