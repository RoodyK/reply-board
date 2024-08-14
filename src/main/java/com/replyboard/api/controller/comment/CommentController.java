package com.replyboard.api.controller.comment;

import com.replyboard.api.controller.comment.request.CreateCommentRequest;
import com.replyboard.api.controller.comment.request.EditCommentRequest;
import com.replyboard.api.controller.comment.request.RemoveCommentRequest;
import com.replyboard.api.dto.ApiDataResponse;
import com.replyboard.api.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.url-prefix}")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiDataResponse<Long>> addComment(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        Long commendId = commentService.addComment(postId, request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.of(commendId));
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/replies")
    public ResponseEntity<ApiDataResponse<Long>> addReply(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        Long replyId = commentService.addReply(postId, commentId, request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.of(replyId));
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public ResponseEntity<ApiDataResponse<Object>> removeComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody RemoveCommentRequest request
    ) {
        commentService.removeComment(postId, commentId, request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.empty());
    }

    @PatchMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<ApiDataResponse<Object>> updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody EditCommentRequest request
    ) {
        commentService.editComment(postId, commentId, request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.empty());
    }
}
