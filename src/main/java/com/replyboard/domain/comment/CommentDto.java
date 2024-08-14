package com.replyboard.domain.comment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentDto {

    private String author;
    private String password;
    private String content;

    @Builder
    public CommentDto(String author, String password, String content) {
        this.author = author;
        this.password = password;
        this.content = content;
    }
}
