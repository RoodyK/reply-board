package com.replyboard.api.service.post.request;

import com.replyboard.domain.post.PostDto;
import com.replyboard.domain.post.PostStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EditPostServiceRequest {

    private Long categoryId;
    private String title;
    private String content;
    private PostStatus postStatus;

    @Builder
    public EditPostServiceRequest(Long categoryId, String title, String content, PostStatus postStatus) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.postStatus = postStatus == null ? PostStatus.PUBLIC : postStatus;
    }

}
