package com.replyboard.api.service.comment.response;

import com.replyboard.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class CommentResponse {

    private final Long id;
    private final Long parentId;
    private final String author;
    private final String content;
    private final List<CommentResponse> replies;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.author = comment.getAuthor();
        this.content = comment.getContent();
        this.replies = comment.getReplies().stream()
                .map(reply -> new CommentResponse(
                        reply.getId(),
                        reply.getParent().getId(),
                        reply.getAuthor(),
                        reply.getContent(),
                        null // 대댓글에 또다른 댓글을 달 수 없음
                )).collect(Collectors.toList());
    }

    @Builder
    public CommentResponse(Long id, Long parentId, String author, String content, List<CommentResponse> replies) {
        this.id = id;
        this.parentId = parentId;
        this.author = author;
        this.content = content;
        this.replies = replies;
    }
}
