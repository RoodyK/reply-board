package com.replyboard.domain.post;

import com.replyboard.IntegrationTestSupport;
import com.replyboard.api.controller.post.request.CreatePostRequest;
import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.category.CategoryRepository;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.member.Role;
import com.replyboard.exception.PostNotFoundException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class PostRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @BeforeEach
//    void tearDown() {
//        postRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//        memberRepository.deleteAllInBatch();
//    }

    @DisplayName("전체 게시글을 최신글을 기준으로 페이징 처리해서 조회한다.")
    @Test
    void findPostList() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타1", member);
        Category category2 = createCategory("기타2", member);
        Category category3 = createCategory("기타3", member);
        categoryRepository.saveAll(List.of(category1, category2, category3));

        createPost(member, category1, category2, category3);

        // when
        Page<Post> postList = postRepository.findPostList(new PostSearch(null, 1));

        // then
        assertThat(postList).hasSize(10)
                .extracting("title", "content", "category.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목30", "내용30", "기타3"),
                        Tuple.tuple("제목29", "내용29", "기타2"),
                        Tuple.tuple("제목28", "내용28", "기타1"),
                        Tuple.tuple("제목27", "내용27", "기타3"),
                        Tuple.tuple("제목26", "내용26", "기타2"),
                        Tuple.tuple("제목25", "내용25", "기타1"),
                        Tuple.tuple("제목24", "내용24", "기타3"),
                        Tuple.tuple("제목23", "내용23", "기타2"),
                        Tuple.tuple("제목22", "내용22", "기타1"),
                        Tuple.tuple("제목21", "내용21", "기타3")
                );
    }

    @DisplayName("전체 게시글을 최신글을 기준으로 페이징 처리해서 조회할 떄 검색어가 존재하면 해당되는 게시글만 출력한다.")
    @Test
    void findPostListSearchValue() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타1", member);
        Category category2 = createCategory("기타2", member);
        Category category3 = createCategory("기타3", member);
        categoryRepository.saveAll(List.of(category1, category2, category3));

        createPost(member, category1, category2, category3);

        // when
        Page<Post> postList = postRepository.findPostList(new PostSearch("내용2", 1));

        // then
        assertThat(postList).hasSize(10)
                .extracting("title", "content", "category.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목29", "내용29", "기타2"),
                        Tuple.tuple("제목28", "내용28", "기타1"),
                        Tuple.tuple("제목27", "내용27", "기타3"),
                        Tuple.tuple("제목26", "내용26", "기타2"),
                        Tuple.tuple("제목25", "내용25", "기타1"),
                        Tuple.tuple("제목24", "내용24", "기타3"),
                        Tuple.tuple("제목23", "내용23", "기타2"),
                        Tuple.tuple("제목22", "내용22", "기타1"),
                        Tuple.tuple("제목21", "내용21", "기타3"),
                        Tuple.tuple("제목20", "내용20", "기타2")
                );
    }

    @DisplayName("카테고리별 전체 게시글을 최신글을 기준으로 페이징 처리해서 조회한다.")
    @Test
    void findPostListByCategory() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타1", member);
        Category category2 = createCategory("기타2", member);
        Category category3 = createCategory("기타3", member);
        categoryRepository.saveAll(List.of(category1, category2, category3));

        createPost(member, category1, category2, category3);

        // when
        Page<Post> postList = postRepository.findPostList(category3.getId(), new PostSearch(null, 1));

        // then
        assertThat(postList).hasSize(10)
                .extracting("title", "content", "category.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목30", "내용30", "기타3"),
                        Tuple.tuple("제목27", "내용27", "기타3"),
                        Tuple.tuple("제목24", "내용24", "기타3"),
                        Tuple.tuple("제목21", "내용21", "기타3"),
                        Tuple.tuple("제목18", "내용18", "기타3"),
                        Tuple.tuple("제목15", "내용15", "기타3"),
                        Tuple.tuple("제목12", "내용12", "기타3"),
                        Tuple.tuple("제목9", "내용9", "기타3"),
                        Tuple.tuple("제목6", "내용6", "기타3"),
                        Tuple.tuple("제목3", "내용3", "기타3")
                );
    }

    @DisplayName("카테고리별 전체 게시글을 최신글을 기준으로 페이징 처리해서 조회할 때 검색어가 존재하면 해당되는 게시글만 출력한다")
    @Test
    void findPostListByCategorySearchValue() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("기타1", member);
        Category category2 = createCategory("기타2", member);
        Category category3 = createCategory("기타3", member);
        categoryRepository.saveAll(List.of(category1, category2, category3));

        createPost(member, category1, category2, category3);

        // when
        Page<Post> postList = postRepository.findPostList(category3.getId(), new PostSearch("내용2", 1));

        // then
        assertThat(postList).hasSize(3)
                .extracting("title", "content", "category.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목27", "내용27", "기타3"),
                        Tuple.tuple("제목24", "내용24", "기타3"),
                        Tuple.tuple("제목21", "내용21", "기타3")
                );
    }

    @DisplayName("게시글을 단건 조회한다.")
    @Test
    void findByPost() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("기타", member);
        categoryRepository.save(category);

        CreatePostRequest postRequest = createPostRequest("무더위", "날씨가 이상해요");
        Post post = Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category);
        postRepository.save(post);

        // when
        Post findPost = postRepository.findByPost(post.getId()).orElseThrow(PostNotFoundException::new);

        // then
        assertThat(findPost).isNotNull();
        assertThat(findPost.getCategory().getName()).isEqualTo("기타");
        assertThat(findPost.getMember().getName()).isEqualTo("루디");
        assertThat(findPost.getTitle()).isEqualTo("무더위");
        assertThat(findPost.getContent()).isEqualTo("날씨가 이상해요");
    }

    private void createPost(Member member, Category category1, Category category2, Category category3) {
        IntStream.range(1, 31)
                .mapToObj(i -> {
                    CreatePostRequest postRequest = createPostRequest("제목" + i, "내용" + i);
                    if (i % 3 == 1) return Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category1);
                    if (i % 3 == 2) return Post.createPost(postRequest.toServiceRequest().toPostDto(), member, category2);
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