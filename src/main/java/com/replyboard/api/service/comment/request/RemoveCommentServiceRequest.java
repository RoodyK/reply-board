package com.replyboard.api.service.comment.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoveCommentServiceRequest {

    private String password;

    @Builder
    public RemoveCommentServiceRequest(String password) {
        this.password = password;
    }
}
