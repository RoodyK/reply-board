package com.replyboard.api.service.category;

import com.replyboard.api.controller.category.request.CreateCategoryRequest;
import com.replyboard.api.controller.category.request.EditCategoryRequest;
import com.replyboard.api.service.category.response.CategoryResponse;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.category.CategoryRepository;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.member.Role;
import com.replyboard.exception.CategoryNotFoundException;
import com.replyboard.exception.DuplicatedCategoryException;
import com.replyboard.exception.InvalidRequestException;
import com.replyboard.exception.MemberNotFoundException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void tearDown() {
        categoryRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("카테고리를 등록한다.")
    @Test
    void addCategory() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        CreateCategoryRequest request = createCategoryRequest("기타");

        // when
        Long savedId = categoryService.addCategory(member.getId(), request.toServiceRequest());

        // then
        assertThat(savedId).isNotNull();
        Category findCategory = categoryRepository.findAll().get(0);
        assertThat(findCategory).isNotNull();
        assertThat(findCategory.getName()).isEqualTo("기타");
    }

    @DisplayName("카테고리를 등록할 때 카테고리명은 비어있을 수 없다.")
    @Test
    void addCategoryWithoutName() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        CreateCategoryRequest request = createCategoryRequest(null);

        // when
        assertThatThrownBy(() -> categoryService.addCategory(member.getId(), request.toServiceRequest()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("카테고리명은 빈 값일 수 없습니다.");
    }

    @DisplayName("카테고리를 등록할 때 카테고리는 숫자, 한글, 영문자만 가능하다.")
    @Test
    void addCategoryInvalidPatternName() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        CreateCategoryRequest request = createCategoryRequest("카테고리!@#");

        // when
        assertThatThrownBy(() -> categoryService.addCategory(member.getId(), request.toServiceRequest()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("카테고리명은 영문자, 숫자, 한글만 가능합니다.");
    }

    @DisplayName("카테고리를 등록할 때 존재하지 않는 회원은 등록할 수 없다.")
    @Test
    void addCategoryWithoutMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        CreateCategoryRequest request = createCategoryRequest("기타");

        // when
        assertThatThrownBy(() -> categoryService.addCategory(member.getId() + 1, request.toServiceRequest()))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("회원을 찾을 수 없습니다.");
    }

    @DisplayName("카테고리를 등록할 때 중복된 카테고리명으로 등록할 수 없다.")
    @Test
    void addCategoryDuplicatedMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        CreateCategoryRequest request = createCategoryRequest("기타");

        // when
        assertThatThrownBy(() -> categoryService.addCategory(member.getId(), request.toServiceRequest()))
                .isInstanceOf(DuplicatedCategoryException.class)
                .hasMessage("중복된 카테고리명이 존재합니다.");
    }

    @DisplayName("카테고리 목록을 조회한다.")
    @Test
    void getCategories() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타", member);
        Category category2 = createCategory("요리", member);
        categoryRepository.saveAll(List.of(category1, category2));

        // when
        List<CategoryResponse> categories = categoryService.getCategories();

        // then
        assertThat(categories).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder(
                        "기타", "요리"
                );
        List<Category> result = categoryRepository.findAll();
        assertThat(result).hasSize(2);
    }

    @DisplayName("키테고리를 제거한다")
    @Test
    void removeCategory() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        // when
        categoryService.removeCategory(category.getId());

        // then
        List<Category> findCategories = categoryRepository.findAll();
        assertThat(findCategories).isEmpty();
    }

    @DisplayName("카테고리를 제거할 때 존재하는 카테고리가 없으면 예외가 발생한다.")
    @Test
    void removeCategoryWithoutCategory() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        // when
        assertThatThrownBy(() -> categoryService.removeCategory(category.getId() + 1))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }

    @DisplayName("카테고리를 수정한다.")
    @Test
    void editCategory() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        EditCategoryRequest request = editCategoryRequest("요리");

        // when
        categoryService.editCategory(category.getId(), request.toServiceRequest());

        // then
        Category findCategory = categoryRepository.findAll().get(0);
        assertThat(findCategory).isNotNull();
        assertThat(findCategory.getName()).isEqualTo("요리");
    }

    @DisplayName("카테고리를 수정할 때 카테고리명은 한글, 숫자, 영문자만 가능하다.")
    @Test
    void editCategoryInvalidCategoryName() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        EditCategoryRequest request = editCategoryRequest("요리!@#");

        // when
        assertThatThrownBy(() -> categoryService.editCategory(category.getId(), request.toServiceRequest()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("카테고리명은 영문자, 숫자, 한글만 가능합니다.");
    }

    @DisplayName("카테고리를 수정할 때 ID에 해당하는 카테고리가 존재하지 않으면 예외가 발생한다.")
    @Test
    void editCategoryNotExistsCategory() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        EditCategoryRequest request = editCategoryRequest("요리");

        // when
        assertThatThrownBy(() -> categoryService.editCategory(category.getId() + 1, request.toServiceRequest()))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }

    @DisplayName("카테고리를 수정할 때 요청한 카테고리명이 이미 존재하면 예외가 발생한다.")
    @Test
    void editCategoryDuplicatedCategory() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타", member);
        Category category2 = createCategory("요리", member);
        categoryRepository.saveAll(List.of(category1, category2));

        EditCategoryRequest request = editCategoryRequest("요리");

        // when
        assertThatThrownBy(() -> categoryService.editCategory(category1.getId(), request.toServiceRequest()))
                .isInstanceOf(DuplicatedCategoryException.class)
                .hasMessage("중복된 카테고리명이 존재합니다.");
    }

    private Member createMember() {
        Member member = Member.builder()
                .name("루디")
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .build();

        member.addRole(Set.of(Role.ROLE_ADMIN));

        return member;
    }

    private Category createCategory(String name, Member member) {
        Category category = Category.builder()
                .name(name)
                .build();
        category.addMember(member);

        return category;
    }

    private CreateCategoryRequest createCategoryRequest(String name) {
        return CreateCategoryRequest.builder()
                .name(name)
                .build();
    }

    private EditCategoryRequest editCategoryRequest(String name) {
        return EditCategoryRequest.builder()
                .name(name)
                .build();
    }
}