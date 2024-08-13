package com.replyboard.domain.post;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.config.CustomProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.replyboard.domain.category.QCategory.category;
import static com.replyboard.domain.post.QPost.post;

@Slf4j
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final Integer pageSize;

    public PostRepositoryCustomImpl(CustomProperties customProperties, JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
        pageSize = customProperties.getPageSize();
    }

    @Override
    public Page<Post> findPostList(PostSearch postSearch) {
        List<Post> posts = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.category, category).fetchJoin()
                .where(
                        postEqPublic(),
                        postSearchCondition(postSearch)
                )
                .offset(postSearch.getOffset(pageSize))
                .limit(pageSize)
                .orderBy(post.id.desc())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(
                        postEqPublic(),
                        postSearchCondition(postSearch)
                )
                .fetchFirst();

        return new PageImpl<>(posts, postSearch.getPageable(pageSize), totalCount);
    }

    @Override
    public Page<Post> findPostList(Long categoryId, PostSearch postSearch) {
        List<Post> posts = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.category, category).fetchJoin()
                .where(
                        categoryIdEq(categoryId),
                        postEqPublic(),
                        postSearchCondition(postSearch)
                )
                .offset(postSearch.getOffset(pageSize))
                .limit(pageSize)
                .orderBy(post.id.desc())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(
                        categoryIdEq(categoryId),
                        postEqPublic(),
                        postSearchCondition(postSearch)
                )
                .fetchFirst();

        return new PageImpl<>(posts, postSearch.getPageable(pageSize), totalCount);
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return post.category.id.eq(categoryId);
    }

    private BooleanExpression postEqPublic() {
        return post.postStatus.eq(PostStatus.PUBLIC);
    }

    private BooleanExpression postSearchCondition(PostSearch postSearch) {
        BooleanExpression predicate = null;

        BooleanExpression titlePredicate = titleLike(postSearch.getSearchValue());
        BooleanExpression contentPredicate = contentLike(postSearch.getSearchValue());

        if (titlePredicate != null) {
            predicate = titlePredicate;
        }

        if (contentPredicate != null) {
            predicate = (predicate != null) ? predicate.or(contentPredicate) : contentPredicate;
        }

        return predicate;
    }

    private BooleanExpression titleLike(String searchValue) {
        return StringUtils.hasText(searchValue) ? post.title.contains(searchValue) : null;
    }

    private BooleanExpression contentLike(String searchValue) {
        return StringUtils.hasText(searchValue) ? post.content.contains(searchValue) : null;
    }
}
