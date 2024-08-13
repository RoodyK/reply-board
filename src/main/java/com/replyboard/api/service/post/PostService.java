package com.replyboard.api.service.post;

import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.api.dto.PagingResponse;
import com.replyboard.api.service.post.response.PostResponse;
import com.replyboard.domain.post.Post;
import com.replyboard.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 전체 게시글 조회
     */
    @Transactional(readOnly = true)
    public PagingResponse<PostResponse> getPostList(PostSearch postSearch) {
        Page<Post> postList = postRepository.findPostList(postSearch);

        return new PagingResponse<>(postList, PostResponse.class);
    }

    /**
     * 카테고리별 게시글 조회
     *
     * @return
     */
    @Transactional(readOnly = true)
    public PagingResponse<PostResponse> getPostList(Long categoryId, PostSearch postSearch) {
        Page<Post> postList = postRepository.findPostList(categoryId, postSearch);

        return new PagingResponse<>(postList, PostResponse.class);
    }
}
