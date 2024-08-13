package com.replyboard.domain.post;

import com.replyboard.domain.category.Category;
import com.replyboard.domain.member.Member;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    void createPost() {
        // given
        PostDto dto = PostDto.builder()
                .title("제목")
                .content("내용")
                .build();

        Member member = Member.builder()
                .name("루디")
                .email("test@test.com")
                .password("1234")
                .build();

        Category category = Category.builder()
                .name("기타")
                .build();

        // when
        Post post = Post.createPost(dto, member, category);

        // then
        assertThat(post).isNotNull();
        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("내용");
    }
}