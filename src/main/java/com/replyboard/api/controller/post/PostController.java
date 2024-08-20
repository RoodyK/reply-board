package com.replyboard.api.controller.post;

import com.replyboard.api.controller.post.request.CreatePostRequest;
import com.replyboard.api.controller.post.request.EditPostRequest;
import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.api.dto.ApiDataResponse;
import com.replyboard.api.dto.MemberDto;
import com.replyboard.api.dto.PagingResponse;
import com.replyboard.api.security.auth.CustomUserDetails;
import com.replyboard.api.service.post.PostService;
import com.replyboard.api.service.post.response.PostDetailResponse;
import com.replyboard.api.service.post.response.PostResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.url-prefix}")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 전체 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<ApiDataResponse<PagingResponse<PostResponse>>> getPostList(@Valid @ModelAttribute PostSearch postSearch) {
        PagingResponse<PostResponse> response = postService.getPostList(postSearch);

        return ResponseEntity.ok().body(ApiDataResponse.of(response));
    }

    // 카테고리 별 게시글 조회
    @GetMapping("/categories/{categoryId}/posts")
    public ResponseEntity<ApiDataResponse<PagingResponse<PostResponse>>> getPostListByCategory(
            @PathVariable("categoryId") Long categoryId,
            @Valid @ModelAttribute PostSearch postSearch
    ) {
        PagingResponse<PostResponse> response = postService.getPostList(categoryId, postSearch);

        return ResponseEntity.ok().body(ApiDataResponse.of(response));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiDataResponse<PostDetailResponse>> getPost(@PathVariable("postId") Long postId) {
        PostDetailResponse response = postService.getPost(postId);

        return ResponseEntity.ok().body(ApiDataResponse.of(response));
    }

    @GetMapping("/posts/{postId}/member")
    public ResponseEntity<ApiDataResponse<PostDetailResponse>> getMemberWritePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("postId") Long postId
    ) {
        MemberDto memberDto = userDetails.getMemberDto();
        PostDetailResponse response = postService.getMemberWritePost(postId, memberDto.getId());

        return ResponseEntity.ok().body(ApiDataResponse.of(response));
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiDataResponse<Long>> addPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePostRequest request
    ) {
        MemberDto memberDto = userDetails.getMemberDto();
        Long savedId = postService.addPost(memberDto.getId(), request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.of(savedId));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiDataResponse<Void>> removePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("postId") Long postId
    ) {
        MemberDto memberDto = userDetails.getMemberDto();
        postService.removePost(postId, memberDto.getId());

        return ResponseEntity.ok().body(ApiDataResponse.empty());
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<ApiDataResponse<Void>> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody EditPostRequest request
    ) {
        MemberDto memberDto = userDetails.getMemberDto();
        postService.editPost(postId, memberDto.getId(), request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.empty());
    }
}
