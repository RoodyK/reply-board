package com.replyboard.api.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replyboard.api.controller.post.request.CreatePostRequest;
import com.replyboard.api.controller.post.request.EditPostRequest;
import com.replyboard.api.dto.MemberDto;
import com.replyboard.api.security.auth.CustomUserDetails;
import com.replyboard.config.admin.CustomMockRoleAdmin;
import com.replyboard.domain.category.Category;
import com.replyboard.domain.category.CategoryRepository;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.domain.member.Role;
import com.replyboard.domain.post.Post;
import com.replyboard.domain.post.PostRepository;
import com.replyboard.domain.post.PostStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.replyboard.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class DocsPostControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("게시글 전체 조회 API 문서화")
    @Test
    void postList() throws Exception {
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory(member);
        categoryRepository.save(category);

        Post post1 = createPost(member, category, "여름", "지금은 여름이다.");
        Post post2 = createPost(member, category, "가을", "이제는 가을이다.");
        postRepository.saveAll(List.of(post1, post2));

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("searchValue", "이다")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "post-list",
                        RequestDocumentation.queryParameters(
                                RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                                RequestDocumentation.parameterWithName("searchValue").description("검색어").optional()
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("result").description("응답 결과"),
                                PayloadDocumentation.fieldWithPath("code").description("응답 코드"),
                                PayloadDocumentation.fieldWithPath("message").description("응답 메시지"),
                                PayloadDocumentation.fieldWithPath("data.page").description("페이지 번호"),
                                PayloadDocumentation.fieldWithPath("data.pageSize").description("표시될 페이지 크기"),
                                PayloadDocumentation.fieldWithPath("data.totalCount").description("총 게시글 수"),
                                PayloadDocumentation.fieldWithPath("data.items[].categoryId").description("카테고리 ID"),
                                PayloadDocumentation.fieldWithPath("data.items[].categoryName").description("카테고리명"),
                                PayloadDocumentation.fieldWithPath("data.items[].id").description("게시글 ID"),
                                PayloadDocumentation.fieldWithPath("data.items[].title").description("게시글 제목"),
                                PayloadDocumentation.fieldWithPath("data.items[].content").description("게시글 내용"),
                                PayloadDocumentation.fieldWithPath("data.items[].views").description("게시글 조회수"),
                                PayloadDocumentation.fieldWithPath("data.items[].regDate").description("게시글 등록일")
                        )
                ));
    }

    @DisplayName("카테고리별 게시글 조회 API 문서화")
    @Test
    void postListByCategory() throws Exception {
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory(member);
        categoryRepository.save(category);

        Post post1 = createPost(member, category, "여름", "지금은 여름이다.");
        Post post2 = createPost(member, category, "가을", "이제는 가을이다.");
        postRepository.saveAll(List.of(post1, post2));

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/categories/{categoryId}/posts", category.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("searchValue", "이다")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "post-list-by-category",
                        RequestDocumentation.pathParameters(
                                RequestDocumentation.parameterWithName("categoryId").description("카테고리 ID")
                        ),
                        RequestDocumentation.queryParameters(
                                RequestDocumentation.parameterWithName("page").description("페이지 번호").optional(),
                                RequestDocumentation.parameterWithName("searchValue").description("검색어").optional()
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("result").description("응답 결과"),
                                PayloadDocumentation.fieldWithPath("code").description("응답 코드"),
                                PayloadDocumentation.fieldWithPath("message").description("응답 메시지"),
                                PayloadDocumentation.fieldWithPath("data.page").description("페이지 번호"),
                                PayloadDocumentation.fieldWithPath("data.pageSize").description("표시될 페이지 크기"),
                                PayloadDocumentation.fieldWithPath("data.totalCount").description("총 게시글 수"),
                                PayloadDocumentation.fieldWithPath("data.items[].categoryId").description("카테고리 ID"),
                                PayloadDocumentation.fieldWithPath("data.items[].categoryName").description("카테고리명"),
                                PayloadDocumentation.fieldWithPath("data.items[].id").description("게시글 ID"),
                                PayloadDocumentation.fieldWithPath("data.items[].title").description("게시글 제목"),
                                PayloadDocumentation.fieldWithPath("data.items[].content").description("게시글 내용"),
                                PayloadDocumentation.fieldWithPath("data.items[].views").description("게시글 조회수"),
                                PayloadDocumentation.fieldWithPath("data.items[].regDate").description("게시글 등록일")
                        )
                ));
    }

    @DisplayName("게시글 단건 조회 API 문서화")
    @Test
    void post() throws Exception {
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory(member);
        categoryRepository.save(category);

        Post post = createPost(member, category, "글 등록하기", "첫 게시글 입니다.");
        postRepository.save(post);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "post-inquiry",
                        RequestDocumentation.pathParameters(
                                RequestDocumentation.parameterWithName("postId").description("게시글 ID")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("result").description("응답 결과"),
                                PayloadDocumentation.fieldWithPath("code").description("응답 코드"),
                                PayloadDocumentation.fieldWithPath("message").description("응답 메시지"),
                                PayloadDocumentation.fieldWithPath("data.memberId").description("회원 ID"),
                                PayloadDocumentation.fieldWithPath("data.memberName").description("회원 이름"),
                                PayloadDocumentation.fieldWithPath("data.categoryId").description("카테고리 ID"),
                                PayloadDocumentation.fieldWithPath("data.categoryName").description("카테고리명"),
                                PayloadDocumentation.fieldWithPath("data.id").description("게시글 ID"),
                                PayloadDocumentation.fieldWithPath("data.title").description("게시글 제목"),
                                PayloadDocumentation.fieldWithPath("data.content").description("게시글 내용"),
                                PayloadDocumentation.fieldWithPath("data.views").description("게시글 조회수"),
                                PayloadDocumentation.fieldWithPath("data.regDate").description("게시글 등록일")
                        )
                ));
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글 등록 API 문서화")
    @Test
    void addPost() throws Exception {
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory(member);
        categoryRepository.save(category);

        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(category.getId())
                .title("글 작성")
                .content("내용입니다.")
                .postStatus(PostStatus.PUBLIC)
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "post-create",
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("categoryId").description("카테고리 ID"),
                                PayloadDocumentation.fieldWithPath("title").description("게시글 제목"),
                                PayloadDocumentation.fieldWithPath("content").description("게시글 내용").optional(),
                                PayloadDocumentation.fieldWithPath("postStatus").description("게시글 상태")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("result").description("응답 결과"),
                                PayloadDocumentation.fieldWithPath("code").description("응답 코드"),
                                PayloadDocumentation.fieldWithPath("message").description("응답 메시지"),
                                PayloadDocumentation.fieldWithPath("data").description("등록 게시글 ID")
                        )
                ))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글 수정 API 문서화")
    @Test
    void editPost() throws Exception {
        SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy().getContext();
        Authentication authentication = securityContext.getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        MemberDto memberDto = userDetails.getMemberDto();

        Member member = memberRepository.save(Member.builder()
                .email(memberDto.getEmail())
                .name(memberDto.getName())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .build());

        member.addRole(Set.of(Role.ROLE_ADMIN));

        MemberDto newMemberDto = MemberDto.of(member);
        CustomUserDetails details = new CustomUserDetails(newMemberDto);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
        securityContext.setAuthentication(token);

        Category category = createCategory(member);
        categoryRepository.save(category);

        Post post = createPost(member, category, "글 등록하기", "첫 게시글 입니다.");
        postRepository.save(post);

        EditPostRequest request = EditPostRequest.builder()
                .categoryId(category.getId())
                .title("글 수정하기")
                .content("수정합니다.")
                .postStatus(PostStatus.PUBLIC)
                .build();

        this.mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "post-edit",
                        RequestDocumentation.pathParameters(
                                RequestDocumentation.parameterWithName("postId").description("게시글 ID")
                        ),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("categoryId").description("카테고리 ID"),
                                PayloadDocumentation.fieldWithPath("title").description("게시글 제목").optional(),
                                PayloadDocumentation.fieldWithPath("content").description("게시글 내용").optional(),
                                PayloadDocumentation.fieldWithPath("postStatus").description("게시글 상태")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("result").description("응답 결과"),
                                PayloadDocumentation.fieldWithPath("code").description("응답 코드"),
                                PayloadDocumentation.fieldWithPath("message").description("응답 메시지"),
                                PayloadDocumentation.fieldWithPath("data").description("수정 결과는 없다.")
                        )
                ))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글 삭제 API 문서화")
    @Test
    void removePost() throws Exception {
        SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy().getContext();
        Authentication authentication = securityContext.getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        MemberDto memberDto = userDetails.getMemberDto();

        Member member = memberRepository.save(Member.builder()
                .email(memberDto.getEmail())
                .name(memberDto.getName())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .build());

        member.addRole(Set.of(Role.ROLE_ADMIN));

        MemberDto newMemberDto = MemberDto.of(member);
        CustomUserDetails details = new CustomUserDetails(newMemberDto);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
        securityContext.setAuthentication(token);

        Category category = createCategory(member);
        categoryRepository.save(category);

        Post post = createPost(member, category, "글 등록하기", "첫 게시글 입니다.");
        postRepository.save(post);

        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "post-delete",
                        RequestDocumentation.pathParameters(
                                RequestDocumentation.parameterWithName("postId").description("게시글 ID")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("result").description("응답 결과"),
                                PayloadDocumentation.fieldWithPath("code").description("응답 코드"),
                                PayloadDocumentation.fieldWithPath("message").description("응답 메시지"),
                                PayloadDocumentation.fieldWithPath("data").description("삭제 결과는 없다.")
                        )
                ))
        ;
    }

    private Post createPost(Member member, Category category, String title, String content) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .postStatus(PostStatus.PUBLIC)
                .views(0)
                .build();
        post.addMember(member);
        post.addCategory(category);

        return post;
    }

    private Member createMember() {
        Member member = Member.builder()
                .name("독스")
                .email("docs@test.com")
                .password(passwordEncoder.encode("1234"))
                .build();

        member.addRole(Set.of(Role.ROLE_ADMIN));

        return member;
    }

    private Category createCategory(Member member) {
        Category category = Category.builder()
                .name("기타")
                .build();
        category.addMember(member);

        return category;
    }
}
