package com.replyboard.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //@Query(value = "select c from Comment c where c.post.id = :postId and c.parent is null")
    List<Comment> findByPostIdAndParentIsNull(Long postId);
}
