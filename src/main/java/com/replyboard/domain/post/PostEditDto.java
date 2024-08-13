package com.replyboard.domain.post;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class PostEditDto {

    private String title;
    private String content;
    private PostStatus postStatus;

    public PostEditDto(String title, String content, PostStatus postStatus) {
        this.title = title;
        this.content = content;
        this.postStatus = postStatus;
    }

    public static PostEditDto.PostDtoBuilder builder() {
        return new PostEditDto.PostDtoBuilder();
    }

    public static class PostDtoBuilder {
        private String title;
        private String content;
        private PostStatus postStatus;

        PostDtoBuilder() {
        }

        public PostEditDto.PostDtoBuilder title(final String title) {
            if (StringUtils.hasText(title)) {
                this.title = title;
            }
            return this;
        }

        public PostEditDto.PostDtoBuilder content(final String content) {
            if (StringUtils.hasText(content)) {
                this.content = content;
            }
            return this;
        }

        public PostEditDto.PostDtoBuilder postStatus(final PostStatus postStatus) {
            if (postStatus != null) {
                this.postStatus = postStatus;
            }
            return this;
        }

        public PostEditDto build() {
            return new PostEditDto(this.title, this.content, this.postStatus);
        }

    }

}
