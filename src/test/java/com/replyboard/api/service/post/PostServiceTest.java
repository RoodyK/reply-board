package com.replyboard.api.service.post;

import com.replyboard.api.controller.post.request.CreatePostRequest;
import com.replyboard.api.controller.post.request.EditPostRequest;
import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.api.dto.PagingResponse;
import com.replyboard.api.service.post.response.PostDetailResponse;
import com.replyboard.api.service.post.response.PostResponse;
import com.replyboard.config.CustomProperties;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.category.CategoryRepository;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.member.Role;
import com.replyboard.domain.post.Post;
import com.replyboard.domain.post.PostRepository;
import com.replyboard.domain.post.PostStatus;
import com.replyboard.exception.CategoryNotFoundException;
import com.replyboard.exception.MemberNotFoundException;
import com.replyboard.exception.PostNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
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

//    @BeforeEach
//    void tearDown() {
//        postRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//        memberRepository.deleteAllInBatch();
//    }

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
        Member member = createMember("루디", "test@test.com");
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
        Member member = createMember("루디", "test@test.com");
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
        Member member = createMember("루디", "test@test.com");
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

    @DisplayName("게시글을 등록한다.")
    @Test
    void addPost() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        // when
        Long savedId = postService.addPost(member.getId(), request.toServiceRequest());

        // then
        assertThat(savedId).isNotNull();
        Post findPost = postRepository.findAll().get(0);
        assertThat(findPost.getTitle()).isEqualTo("글 등록하기");
        assertThat(findPost.getContent()).isEqualTo("첫 게시글 입니다.");
        assertThat(findPost.getId()).isEqualTo(savedId);
        assertThat(findPost.getMember()).isEqualTo(member);
        assertThat(findPost.getCategory()).isEqualTo(category);
    }

    @DisplayName("게시글을 등록 시 회원이 존재하지 않으면 예외가 발생한다.")
    @Test
    void addPostNotExistsMemberId() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        // when
        assertThatThrownBy(() -> postService.addPost(member.getId() + 1L, request.toServiceRequest()))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("회원을 찾을 수 없습니다.");
    }

    @DisplayName("게시글을 등록 시 카테고리가 존재하지 않으면 예외가 발생한다.")
    @Test
    void addPostNotExistsCategory() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId() + 1L)
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        // when
        assertThatThrownBy(() -> postService.addPost(member.getId(), request.toServiceRequest()))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }

    @DisplayName("게시글을 제거한다.")
    @Test
    void removePost() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId() + 1L)
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        // when
        postService.removePost(post.getId());

        // then
        List<Post> postList = postRepository.findAll();
        assertThat(postList).isEmpty();
    }

    @DisplayName("게시글을 제거할 때 게시글이 존재하지 않으면 예외가 발생한다.")
    @Test
    void removePostNotExistsPostId() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId() + 1L)
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        // when
        assertThatThrownBy(() -> postService.removePost(post.getId() + 1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
    }

    @DisplayName("게시글을 수정한다.")
    @Test
    void editPost() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        Category category2 = createCategory("요리", member);
        categoryRepository.save(category);
        categoryRepository.save(category2);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        EditPostRequest editRequest = EditPostRequest.builder()
                .categoryId(category2.getId())
                .title("글 수정")
                .content("글 수정하기")
                .postStatus(PostStatus.PUBLIC)
                .build();

        // when
        postService.editPost(post.getId(), member.getId(), editRequest.toServiceRequest());

        // then
        Post findPost = postRepository.findAll().get(0);
        assertThat(findPost.getCategory().getId()).isEqualTo(category2.getId());
        assertThat(findPost.getTitle()).isEqualTo("글 수정");
        assertThat(findPost.getContent()).isEqualTo("글 수정하기");
        assertThat(findPost.getPostStatus()).isEqualTo(PostStatus.PUBLIC);
    }

    @DisplayName("게시글을 수정할 때 제목이 빈값이면 이전 값을 유지한다.")
    @Test
    void editPostWithoutTitle() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        Category category2 = createCategory("요리", member);
        categoryRepository.save(category);
        categoryRepository.save(category2);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        EditPostRequest editRequest = EditPostRequest.builder()
                .categoryId(category2.getId())
                .title(null)
                .content("글 수정하기")
                .postStatus(PostStatus.PUBLIC)
                .build();

        // when
        postService.editPost(post.getId(), member.getId(), editRequest.toServiceRequest());

        // then
        Post findPost = postRepository.findAll().get(0);
        assertThat(findPost.getCategory().getId()).isEqualTo(category2.getId());
        assertThat(findPost.getTitle()).isEqualTo("글 등록하기");
        assertThat(findPost.getContent()).isEqualTo("글 수정하기");
        assertThat(findPost.getPostStatus()).isEqualTo(PostStatus.PUBLIC);
    }

    @DisplayName("게시글을 수정할 때 내용이 빈값이면 이전 값을 유지한다.")
    @Test
    void editPostWithoutContent() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        Category category2 = createCategory("요리", member);
        categoryRepository.save(category);
        categoryRepository.save(category2);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        EditPostRequest editRequest = EditPostRequest.builder()
                .categoryId(category2.getId())
                .title("글 수정")
                .content(null)
                .postStatus(PostStatus.PUBLIC)
                .build();

        // when
        postService.editPost(post.getId(), member.getId(), editRequest.toServiceRequest());

        // then
        Post findPost = postRepository.findAll().get(0);
        assertThat(findPost.getCategory().getId()).isEqualTo(category2.getId());
        assertThat(findPost.getTitle()).isEqualTo("글 수정");
        assertThat(findPost.getContent()).isEqualTo("첫 게시글 입니다.");
        assertThat(findPost.getPostStatus()).isEqualTo(PostStatus.PUBLIC);
    }

    @DisplayName("게시글을 수정할 때 카테고리가 이전과 같으면 유지된다.")
    @Test
    void editPosSameCategory() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        EditPostRequest editRequest = EditPostRequest.builder()
                .categoryId(category.getId())
                .title("글 수정")
                .content(null)
                .postStatus(PostStatus.PUBLIC)
                .build();

        // when
        postService.editPost(post.getId(), member.getId(), editRequest.toServiceRequest());

        // then
        Post findPost = postRepository.findAll().get(0);
        assertThat(findPost.getCategory().getId()).isEqualTo(category.getId());
        assertThat(findPost.getTitle()).isEqualTo("글 수정");
        assertThat(findPost.getContent()).isEqualTo("첫 게시글 입니다.");
        assertThat(findPost.getPostStatus()).isEqualTo(PostStatus.PUBLIC);
    }

    @DisplayName("게시글을 수정할 때 수정하려는 게시글이 존재하지 않으면 예외가 발생한다.")
    @Test
    void editPostNotExistsPost() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        EditPostRequest editRequest = EditPostRequest.builder()
                .title("글 수정")
                .content(null)
                .postStatus(PostStatus.PUBLIC)
                .build();

        // when
        assertThatThrownBy(() -> postService.editPost(post.getId() + 1L, member.getId(), editRequest.toServiceRequest()))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
    }

    @DisplayName("게시글을 수정할 때 수정하려는 카테고리가 존재하지 않으면 예외가 발생한다.")
    @Test
    void editPostNotExistsCategory() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        EditPostRequest editRequest = EditPostRequest.builder()
                .categoryId(category.getId() + 1L)
                .title("글 수정")
                .content(null)
                .postStatus(PostStatus.PUBLIC)
                .build();

        // when
        assertThatThrownBy(() -> postService.editPost(post.getId(), member.getId(), editRequest.toServiceRequest()))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }

    @DisplayName("게시글을 상세 조회한다.")
    @Test
    void getPost() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        // when
        PostDetailResponse response = postService.getPost(post.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMemberName()).isEqualTo("루디");
        assertThat(response.getCategoryName()).isEqualTo("기타1");
        assertThat(response.getTitle()).isEqualTo("글 등록하기");
        assertThat(response.getContent()).isEqualTo("첫 게시글 입니다.");
    }

    @DisplayName("게시글을 상세 조회할 때 게시글이 존재하지 않으면 예외가 발생한다.")
    @Test
    void getPostNotExistsPost() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .build();

        Post post = Post.createPost(request.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        // when
        assertThatThrownBy(() -> postService.getPost(post.getId() + 1))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
    }

    @DisplayName("게시글을 상세 조회할 때 공개 게시글만 조회가 가능하다")
    @Test
    void getPostNotOwnPost() {
        // given
        Member member = createMember("루디", "test@test.com");
        memberRepository.save(member);

        Category category = createCategory("기타1", member);
        categoryRepository.save(category);

        Post post = Post.builder()
                .title("글 등록하기")
                .content("첫 게시글 입니다.")
                .postStatus(PostStatus.PRIVATE)
                .build();
        post.addMember(member);
        post.addCategory(category);

        postRepository.save(post);

        // when
        assertThatThrownBy(() -> postService.getPost(post.getId()))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
    }

    private void createPostList() {
        Member member = createMember("루디", "test@test.com");
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

    private Member createMember(String name, String email) {
        Member member = Member.builder()
                .name(name)
                .email(email)
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