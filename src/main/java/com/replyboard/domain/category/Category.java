package com.replyboard.domain.category;

import com.replyboard.domain.BaseEntity;
import com.replyboard.domain.member.Member;
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
@Table(
        name = "category",
        indexes = {
                @Index(name = "idx_category_name", columnList = "name")
        }
)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "category")
    public List<Post> posts = new ArrayList<>();

    @Builder
    public Category(String name) {
        this.name = name;
    }

    public void addMember(Member member) {
        this.member = member;
    }

    public void edit(String name) {
        this.name = name;
    }
}
