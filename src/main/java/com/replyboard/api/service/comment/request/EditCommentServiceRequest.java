package com.replyboard.api.service.comment.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EditCommentServiceRequest {

    private String password;
    private String content;

    @Builder
    public EditCommentServiceRequest(String password, String content) {
        this.password = password;
        this.content = content;
    }
}
