package com.replyboard.api.service.comment;

import com.replyboard.api.controller.comment.request.EditCommentRequest;
import com.replyboard.api.service.comment.request.CreateCommentServiceRequest;
import com.replyboard.api.service.comment.request.EditCommentServiceRequest;
import com.replyboard.api.service.comment.request.RemoveCommentServiceRequest;
import com.replyboard.domain.comment.Comment;
import com.replyboard.domain.comment.CommentRepository;
import com.replyboard.domain.post.Post;
import com.replyboard.domain.post.PostRepository;
import com.replyboard.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long addComment(Long postId, CreateCommentServiceRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        String encryptPassword = passwordEncoder.encode(request.getPassword());

        Comment comment = Comment.createComment(request.toCommentDto(encryptPassword), post);
        Comment savedComment = commentRepository.save(comment);

        return savedComment.getId();
    }

    @Transactional
    public Long addReply(Long postId, Long commentId, CreateCommentServiceRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (parentComment.getParent() != null) {
            throw new CanNotAnotherReplyException();
        }

        String encryptPassword = passwordEncoder.encode(request.getPassword());

        Comment reply = Comment.createReply(request.toCommentDto(encryptPassword), post, parentComment);
        Comment savedReply = commentRepository.save(reply);

        return savedReply.getId();
    }

    @Transactional
    public void removeComment(Long postId, Long commentId, RemoveCommentServiceRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        validSamePostId(postId, comment.getPost().getId());
        validSamePassword(request.getPassword(), comment.getPassword());

        commentRepository.delete(comment);
    }

    @Transactional
    public void editComment(Long postId, Long commentId, EditCommentServiceRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        validSamePostId(postId, comment.getPost().getId());
        validSamePassword(request.getPassword(), comment.getPassword());

        comment.editComment(request.getContent());
    }

    private void validSamePostId(Long postId, Long commentPostId) {
        if (!checkSamePostId(postId, commentPostId)) {
            throw new NotSamePostException();
        }
    }

    private void validSamePassword(String requestPassword, String commentPassword) {
        if (!checkSamePassword(requestPassword, commentPassword)) {
            throw new NotMatchPasswordException();
        }
    }

    private boolean checkSamePostId(Long postId, Long commentPostId) {
        return Objects.equals(postId, commentPostId);
    }

    private boolean checkSamePassword(String requestPassword, String commentPassword) {
        return passwordEncoder.matches(requestPassword, commentPassword);
    }
}
