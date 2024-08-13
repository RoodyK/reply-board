package com.replyboard.api.service.post.response;

import com.replyboard.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final long views;
    private final LocalDateTime regDate;
    private final Long categoryId;
    private final String categoryName;

    @Builder
    public PostResponse(Long id, String title, String content, long views, LocalDateTime regDate, Long categoryId, String categoryName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.views = views;
        this.regDate = regDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.views = post.getViews();
        this.regDate = post.getCreatedAt();
        this.categoryId = post.getCategory().getId();
        this.categoryName = post.getCategory().getName();
    }
}
