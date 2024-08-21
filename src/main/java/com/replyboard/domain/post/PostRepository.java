package com.replyboard.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query("select p from Post p join fetch p.category join fetch p.member where p.id = :postId and p.postStatus = 'PUBLIC'")
    Optional<Post> findByPost(@Param("postId") Long postId);

    @Query("select p from Post p join fetch p.category join fetch p.member where p.id = :postId and p.postStatus = 'PRIVATE'")
    Optional<Post> findByPrivate(@Param("postId") Long postId);

    // 카테고리에 해당하는 게시글이 존재하는지 확인
    long countByCategoryId(Long categoryId);
}
