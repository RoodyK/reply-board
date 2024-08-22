package com.replyboard.domain.post;

import com.replyboard.domain.BaseEntity;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.comment.Comment;
import com.replyboard.domain.member.Member;
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
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    private long views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String content, PostStatus postStatus, long views) {
        this.title = title;
        this.content = content;
        this.postStatus = postStatus;
        this.views = views;
    }

    public static Post createPost(PostDto request, Member member, Category category) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postStatus(request.getPostStatus())
                .views(0)
                .build();

        post.member = member;
        post.category = category;

        return post;
    }

    public void editPost(PostEditDto postDto, Category category) {
        this.title = postDto.getTitle();
        this.content = postDto.getContent();
        this.category = category;
    }

    public PostEditDto.PostDtoBuilder toEditPost() {
        return PostEditDto.builder()
                .title(title)
                .content(content)
                .postStatus(postStatus);
    }

    public void addMember(Member member) {
        this.member = member;
    }

    public void addCategory(Category category) {
        this.category = category;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.addPost(this);
    }

    public long incrementViews() {
        return ++this.views;
    }
}
