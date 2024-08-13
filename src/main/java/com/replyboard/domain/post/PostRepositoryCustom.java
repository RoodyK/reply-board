package com.replyboard.domain.post;

import com.replyboard.api.controller.post.request.PostSearch;
import org.springframework.data.domain.Page;

public interface PostRepositoryCustom {

    Page<Post> findPostList(PostSearch postSearch);

    Page<Post> findPostList(Long categoryId, PostSearch postSearch);
}
