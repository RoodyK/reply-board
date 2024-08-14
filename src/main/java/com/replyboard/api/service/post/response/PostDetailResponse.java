package com.replyboard.api.service.post.response;

import com.replyboard.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDetailResponse {

    private Long memberId;
    private String memberName;

    private Long categoryId;
    private String categoryName;

    private Long id;
    private String title;
    private String content;
    private long views;
    private LocalDateTime regDate;

    @Builder
    public PostDetailResponse(Long memberId, String memberName, Long categoryId, String categoryName, Long id, String title, String content, long views, LocalDateTime regDate) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.id = id;
        this.title = title;
        this.content = content;
        this.views = views;
        this.regDate = regDate;
    }


    public static PostDetailResponse of(Post post) {
        return PostDetailResponse.builder()
                .memberId(post.getMember().getId())
                .memberName(post.getMember().getName())
                .categoryId(post.getCategory().getId())
                .categoryName(post.getCategory().getName())
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .regDate(post.getCreatedAt())
                .build();
    }
}
