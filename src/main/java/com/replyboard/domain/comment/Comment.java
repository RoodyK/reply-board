package com.replyboard.domain.comment;

import com.replyboard.domain.BaseEntity;
import com.replyboard.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String password;

    @Column(length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @Builder
    public Comment(String author, String password, String content) {
        this.author = author;
        this.password = password;
        this.content = content;
    }

    public static Comment createComment(CommentDto commentDto, Post post) {
        Comment comment = Comment.builder()
                .author(commentDto.getAuthor())
                .password(commentDto.getPassword())
                .content(commentDto.getContent())
                .build();

        post.addComment(comment);

        return comment;
    }

    public static Comment createReply(CommentDto commentDto, Post post, Comment parentComment) {
        Comment comment = Comment.builder()
                .author(commentDto.getAuthor())
                .password(commentDto.getPassword())
                .content(commentDto.getContent())
                .build();

        post.addComment(comment);
        comment.addParentComment(parentComment);

        return comment;
    }

    public void addParentComment(Comment parent) {
        this.parent = parent;
    }

    public void addPost(Post post) {
        this.post = post;
    }

    public void editComment(String content) {
        this.content = content;
    }
}
