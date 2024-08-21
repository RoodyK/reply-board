package com.replyboard.api.service.category;

import com.replyboard.api.service.category.request.CreateCategoryServiceRequest;
import com.replyboard.api.service.category.request.EditCategoryServiceRequest;
import com.replyboard.api.service.category.response.CategoryResponse;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.category.CategoryRepository;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.post.PostRepository;
import com.replyboard.exception.CategoryNotFoundException;
import com.replyboard.exception.DuplicatedCategoryException;
import com.replyboard.exception.InvalidRequestException;
import com.replyboard.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long addCategory(final Long memberId, final CreateCategoryServiceRequest request) {
        request.validate();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicatedCategoryException();
        }

        Category savedCategory = categoryRepository.save(request.toEntity(member));

        return savedCategory.getId();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        long count = postRepository.countByCategoryId(categoryId);
        if (count > 0) {
            throw new InvalidRequestException("게시글이 존재하는 카테고리는 제거할 수 없습니다.");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    public void editCategory(Long categoryId, EditCategoryServiceRequest request) {
        request.validate();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicatedCategoryException();
        }

        category.edit(request.getName());
    }
}
