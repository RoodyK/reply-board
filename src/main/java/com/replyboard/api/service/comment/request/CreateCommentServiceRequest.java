package com.replyboard.api.service.comment.request;

import com.replyboard.domain.comment.CommentDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCommentServiceRequest {

    private String author;
    private String password;
    private String content;

    @Builder
    public CreateCommentServiceRequest(String author, String password, String content) {
        this.author = author;
        this.password = password;
        this.content = content;
    }

    public CommentDto toCommentDto(String encryptPassword) {
        return CommentDto.builder()
                .author(author)
                .password(encryptPassword)
                .content(content)
                .build();
    }
}
