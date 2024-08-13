package com.replyboard.api.service.post;

import com.replyboard.api.controller.post.request.CreatePostRequest;
import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.api.dto.PagingResponse;
import com.replyboard.api.service.post.response.PostResponse;
import com.replyboard.config.CustomProperties;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.category.CategoryRepository;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.member.Role;
import com.replyboard.domain.post.Post;
import com.replyboard.domain.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


@ActiveProfiles("test")
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomProperties customProperties;

    @BeforeEach
    void tearDown() {
        postRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("전체 게시글 조회. 1페이지, 검색어 X")
    @Test
    void postList() {
        // given
        createPostList();

        // when
        int page = 1;
        PostSearch postSearch = new PostSearch(null, page);
        PagingResponse<PostResponse> response = postService.getPostList(postSearch);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(customProperties.getPageSize());
        assertThat(response.getTotalCount()).isEqualTo(30);
        assertThat(response.getItems()).hasSize(10)
                .extracting("title", "content", "categoryName")
                .containsExactlyInAnyOrder(
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("여름 지나기", "내용", "기타2"),
                        tuple("오늘 날씨는", "내용", "기타1"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("여름 지나기", "내용", "기타2"),
                        tuple("오늘 날씨는", "내용", "기타1"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("여름 지나기", "내용", "기타2"),
                        tuple("오늘 날씨는", "내용", "기타1"),
                        tuple("소스 만들기", "내용", "기타3")
                );
    }

    @DisplayName("전체 게시글 조회. 1페이지, 검색어 있음")
    @Test
    void postListSearchValue() {
        // given
        createPostList();

        // when
        int page = 1;
        PostSearch postSearch = new PostSearch("소스", page);
        PagingResponse<PostResponse> response = postService.getPostList(postSearch);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(customProperties.getPageSize());
        assertThat(response.getItems()).hasSize(10)
                .extracting("title", "content", "categoryName")
                .containsExactlyInAnyOrder(
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3")
                );
    }

    @DisplayName("전체 게시글 조회. 페이지 번호가 음수면 1페이지가 출력된다.")
    @Test
    void postListNegativePageNumber() {
        // given
        createPostList();

        // when
        int page = -1;
        PostSearch postSearch = new PostSearch("소스", page);
        PagingResponse<PostResponse> response = postService.getPostList(postSearch);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(customProperties.getPageSize());
        assertThat(response.getTotalCount()).isEqualTo(10);
        assertThat(response.getItems()).hasSize(10)
                .extracting("title", "content", "categoryName")
                .containsExactlyInAnyOrder(
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3"),
                        tuple("소스 만들기", "내용", "기타3")
                );
    }

    @DisplayName("키테고리별 게시글 조회 시 게시글은 최신순으로 정렬된다.")
    @Test
    void postListByCategory() {
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타1", member);
        Category category2 = createCategory("기타2", member);
        Category category3 = createCategory("기타3", member);
        categoryRepository.saveAll(List.of(category1, category2, category3));
        // given
        createPostList(member, category1, category2, category3);

        // when
        int page = 1;
        PostSearch postSearch = new PostSearch(null, page);
        PagingResponse<PostResponse> response = postService.getPostList(category1.getId(), postSearch);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(customProperties.getPageSize());
        assertThat(response.getTotalCount()).isEqualTo(10);
        assertThat(response.getItems()).hasSize(10)
                .extracting("title", "content", "categoryName")
                .containsExactlyInAnyOrder(
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1")
                );
    }

    @DisplayName("키테고리별 게시글 조회 시 검색어를 입력하면 해당 게시글만 출력된다.")
    @Test
    void postListByCategorySearchValue() {
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타1", member);
        Category category2 = createCategory("기타2", member);
        Category category3 = createCategory("기타3", member);
        categoryRepository.saveAll(List.of(category1, category2, category3));
        // given
        createPostList(member, category1, category2, category3);

        // when
        int page = 1;
        PostSearch postSearch = new PostSearch("춥다", page);
        PagingResponse<PostResponse> response = postService.getPostList(category1.getId(), postSearch);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(customProperties.getPageSize());
        assertThat(response.getTotalCount()).isEqualTo(5);
        assertThat(response.getItems()).hasSize(5)
                .extracting("title", "content", "categoryName")
                .containsExactlyInAnyOrder(
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1")
                );
    }

    @DisplayName("키테고리별 게시글 조회 시 페이지 번호가 음수면 1페이지가 출력된다.")
    @Test
    void postListByCategoryNegativePageNumber() {
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타1", member);
        Category category2 = createCategory("기타2", member);
        Category category3 = createCategory("기타3", member);
        categoryRepository.saveAll(List.of(category1, category2, category3));
        // given
        createPostList(member, category1, category2, category3);

        // when
        int page = -1;
        PostSearch postSearch = new PostSearch(null, page);
        PagingResponse<PostResponse> response = postService.getPostList(category1.getId(), postSearch);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(customProperties.getPageSize());
        assertThat(response.getTotalCount()).isEqualTo(10);
        assertThat(response.getItems()).hasSize(10)
                .extracting("title", "content", "categoryName")
                .containsExactlyInAnyOrder(
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "덥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1"),
                        tuple("오늘 날씨는", "춥다", "기타1")
                );
    }

    private void createPostList() {
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타1", member);
        Category category2 = createCategory("기타2", member);
        Category category3 = createCategory("기타3", member);
        categoryRepository.saveAll(List.of(category1, category2, category3));

        IntStream.range(1, 31)
                .mapToObj(i -> {
                    if (i % 3 == 1) {
                        CreatePostRequest postRequest = createPostRequest("오늘 날씨는", "내용");
                        return Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category1);
                    }
                    if (i % 3 == 2) {
                        CreatePostRequest postRequest = createPostRequest("여름 지나기", "내용");
                        return Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category2);
                    }
                    CreatePostRequest postRequest = createPostRequest("소스 만들기", "내용");
                    return Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category3);
                })
                .forEach(post -> postRepository.save(post));
    }

    private void createPostList(Member member, Category category1, Category category2, Category category3) {
                IntStream.range(1, 31)
                .mapToObj(i -> {
                    if (i % 3 == 1) {
                        CreatePostRequest postRequest = null;
                        if (i >= 15) {
                            postRequest = createPostRequest("오늘 날씨는", "덥다");
                        } else {
                            postRequest = createPostRequest("오늘 날씨는", "춥다");
                        }
                        return Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category1);
                    }
                    if (i % 3 == 2) {
                        CreatePostRequest postRequest = createPostRequest("여름 지나기", "내용");
                        return Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category2);
                    }
                    CreatePostRequest postRequest = createPostRequest("소스 만들기", "내용");
                    return Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category3);
                })
                .forEach(post -> postRepository.save(post));
    }

    private CreatePostRequest createPostRequest(String title, String content) {
        return CreatePostRequest.builder()
                .title(title)
                .content(content)
                .build();
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
}