package com.replyboard.api.service.post;

import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.api.dto.PagingResponse;
import com.replyboard.api.service.post.request.CreatePostServiceRequest;
import com.replyboard.api.service.post.request.EditPostServiceRequest;
import com.replyboard.api.service.post.response.PostDetailResponse;
import com.replyboard.api.service.post.response.PostResponse;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.category.CategoryRepository;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.post.Post;
import com.replyboard.domain.post.PostEditDto;
import com.replyboard.domain.post.PostRepository;
import com.replyboard.exception.CategoryNotFoundException;
import com.replyboard.exception.MemberNotFoundException;
import com.replyboard.exception.NotOwnPostException;
import com.replyboard.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    /**
     * 전체 게시글 조회
     */
    public PagingResponse<PostResponse> getPostList(PostSearch postSearch) {
        Page<Post> postList = postRepository.findPostList(postSearch);

        return new PagingResponse<>(postList, PostResponse.class);
    }

    /**
     * 카테고리별 게시글 조회
     */
    public PagingResponse<PostResponse> getPostList(Long categoryId, PostSearch postSearch) {
        Page<Post> postList = postRepository.findPostList(categoryId, postSearch);

        return new PagingResponse<>(postList, PostResponse.class);
    }

    public PostDetailResponse getPost(Long postId) {
        Post post = postRepository.findByPost(postId)
                .orElseThrow(PostNotFoundException::new);

        return PostDetailResponse.of(post);
    }

    /**
     * 회원이 자신의 게시글(비공개, 차단 포함) 조회
     */
    public PostDetailResponse getMemberAllPostList(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        validMemberId(memberId, post.getMember().getId());

        return PostDetailResponse.of(post);
    }

    @Transactional
    public Long addPost(Long memberId, CreatePostServiceRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        Post post = Post.createPost(request.toPostDto(), member, category);

        Post savedPost = postRepository.save(post);

        return savedPost.getId();
    }

    @Transactional
    public void removePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        validMemberId(memberId, post.getMember().getId());

        postRepository.delete(post);
    }


    @Transactional
    public void editPost(Long postId, Long memberId, EditPostServiceRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        validMemberId(memberId, post.getMember().getId());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        PostEditDto.PostDtoBuilder postDtoBuilder = post.toEditPost();

        PostEditDto postEditDto = postDtoBuilder
                .title(request.getTitle())
                .content(request.getContent())
                .postStatus(request.getPostStatus())
                .build();

        post.editPost(postEditDto, category);
    }

    @Transactional
    public long incrementViews(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return post.incrementViews();
    }

    private void validMemberId(Long loginId, Long postWriterId) {
        if (!isSameMemberId(loginId, postWriterId)) {
            throw new NotOwnPostException();
        }
    }

    private boolean isSameMemberId(Long memberId, Long postWriterId) {
        return Objects.equals(memberId, postWriterId);
    }
}
