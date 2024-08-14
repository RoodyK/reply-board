package com.replyboard.api.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replyboard.api.controller.post.request.CreatePostRequest;
import com.replyboard.api.controller.post.request.EditPostRequest;
import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.api.dto.PagingResponse;
import com.replyboard.api.service.post.PostService;
import com.replyboard.api.service.post.request.CreatePostServiceRequest;
import com.replyboard.api.service.post.request.EditPostServiceRequest;
import com.replyboard.api.service.post.response.PostDetailResponse;
import com.replyboard.api.service.post.response.PostResponse;
import com.replyboard.config.TestSecurityConfig;
import com.replyboard.config.admin.CustomMockRoleAdmin;
import com.replyboard.config.user.CustomMockRoleUser;
import com.replyboard.domain.post.PostStatus;
import com.replyboard.exception.CategoryNotFoundException;
import com.replyboard.exception.MemberNotFoundException;
import com.replyboard.exception.PostNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import(TestSecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("게시글 전체 목록을 조회한다. 패이지가 없으면 1페이지가 출력된다.")
    @Test
    void postList() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        PostResponse postResponse1 = createPostResponse(currentTime, 1L, "제목1", "내용1");
        PostResponse postResponse2 = createPostResponse(currentTime, 2L, "제목2", "내용2");

        PagingResponse<PostResponse> response = new PagingResponse<>(1, 10, 2, Arrays.asList(postResponse1, postResponse2));

        // given
        BDDMockito.given(postService.getPostList(any(PostSearch.class)))
                .willReturn(response);

        // when
        mockMvc.perform(get("/api/v1/posts")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.items.length()").value(2))
                .andExpect(jsonPath("$.data.items[0].id").value(1L))
                .andExpect(jsonPath("$.data.items[0].title").value("제목1"))
                .andExpect(jsonPath("$.data.items[0].content").value("내용1"))
                .andExpect(jsonPath("$.data.items[0].views").value(0))
                .andExpect(jsonPath("$.data.items[0].categoryId").value(1L))
                .andExpect(jsonPath("$.data.items[0].categoryName").value("기타1"))
                .andExpect(jsonPath("$.data.items[1].id").value(2L))
                .andExpect(jsonPath("$.data.items[1].title").value("제목2"))
                .andExpect(jsonPath("$.data.items[1].content").value("내용2"))
                .andExpect(jsonPath("$.data.items[1].views").value(0))
                .andExpect(jsonPath("$.data.items[1].categoryId").value(1L))
                .andExpect(jsonPath("$.data.items[1].categoryName").value("기타1"))
        ;
    }

    @DisplayName("게시글 전체 목록을 조회한다. 페이지가 0보다 작으면 예외가 발생한다.")
    @Test
    void postListNegativePageNumber() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        PostResponse postResponse1 = createPostResponse(currentTime, 1L, "제목1", "내용1");
        PostResponse postResponse2 = createPostResponse(currentTime, 2L, "제목2", "내용2");

        PagingResponse<PostResponse> response = new PagingResponse<>(1, 10, 2, Arrays.asList(postResponse1, postResponse2));

        // given
        BDDMockito.given(postService.getPostList(any(PostSearch.class)))
                .willReturn(response);

        // when
        mockMvc.perform(get("/api/v1/posts")
                        .param("page", "-1")
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.page").value("페이지는 0 이상이어야 합니다."))
                ;
    }

    @DisplayName("게시글 전체 목록을 조회할 때 검색어에 매칭되는 결과만 출력된다.")
    @Test
    void postListSearchValue() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        PostResponse postResponse1 = createPostResponse(currentTime, 1L, "여름", "내용1");

        PagingResponse<PostResponse> response = new PagingResponse<>(1, 10, 1, Collections.singletonList(postResponse1));

        // given
        BDDMockito.given(postService.getPostList(any(PostSearch.class)))
                .willReturn(response);

        // when
        mockMvc.perform(get("/api/v1/posts")
                        .param("searchValue", "여름")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(1L))
                .andExpect(jsonPath("$.data.items[0].title").value("여름"))
                .andExpect(jsonPath("$.data.items[0].content").value("내용1"))
                .andExpect(jsonPath("$.data.items[0].views").value(0))
                .andExpect(jsonPath("$.data.items[0].categoryId").value(1L))
                .andExpect(jsonPath("$.data.items[0].categoryName").value("기타1"))
        ;
    }

    @DisplayName("카테고리 별 게시글 목록 조회한다.")
    @Test
    void postListByCategory() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        PostResponse postResponse1 = createPostResponse(currentTime, 1L, "여름", "내용1");

        PagingResponse<PostResponse> response = new PagingResponse<>(1, 10, 1, Collections.singletonList(postResponse1));

        // given
        BDDMockito.given(postService.getPostList(anyLong(), any(PostSearch.class)))
                .willReturn(response);

        // when
        mockMvc.perform(get("/api/v1/categories/{categoryId}/posts", 1L)
                        .param("searchValue", "여름")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(1L))
                .andExpect(jsonPath("$.data.items[0].title").value("여름"))
                .andExpect(jsonPath("$.data.items[0].content").value("내용1"))
                .andExpect(jsonPath("$.data.items[0].views").value(0))
                .andExpect(jsonPath("$.data.items[0].categoryId").value(1L))
                .andExpect(jsonPath("$.data.items[0].categoryName").value("기타1"))
        ;
    }

    @DisplayName("카테고리 별 게시글 목록 조회할 때 페이지가 음수면 예외가 발생한다.")
    @Test
    void postListByCategoryNegativePageNumber() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        PostResponse postResponse1 = createPostResponse(currentTime, 1L, "여름", "내용1");

        PagingResponse<PostResponse> response = new PagingResponse<>(1, 10, 1, Collections.singletonList(postResponse1));

        // given
        BDDMockito.given(postService.getPostList(anyLong(), any(PostSearch.class)))
                .willReturn(response);

        // when
        mockMvc.perform(get("/api/v1/categories/{categoryId}/posts", 1L)
                        .param("page", "-1")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.page").value("페이지는 0 이상이어야 합니다."))
        ;
    }


    @CustomMockRoleAdmin
    @DisplayName("게시글을 등록한다.")
    @Test
    void addPost() throws Exception {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(1L)
                .title("글 작성")
                .content("내용입니다.")
                .build();

        BDDMockito.given(postService.addPost(anyLong(), any(CreatePostServiceRequest.class)))
                .willReturn(1L);

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(1L))
        ;

        BDDMockito.then(postService).should().addPost(anyLong(), any(CreatePostServiceRequest.class));
    }

    @CustomMockRoleUser
    @DisplayName("게시글을 등록할 때 제목은 필수다.")
    @Test
    void addPostWithoutTitle() throws Exception {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(1L)
                .title(null)
                .content("내용입니다.")
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
        ;
    }

    @CustomMockRoleUser
    @DisplayName("게시글을 등록할 때 내용은 필수다.")
    @Test
    void addPostWithoutContent() throws Exception {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(1L)
                .title("글 제목")
                .content(null)
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요."))
        ;
    }

    @CustomMockRoleUser
    @DisplayName("게시글을 등록할 때 카테고리 ID는 필수다.")
    @Test
    void addPostWithoutCategoryId() throws Exception {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(null)
                .title("글 제목")
                .content("글 내용")
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.categoryId").value("카테고리는 필수입니다."))
        ;
    }

    @CustomMockRoleUser
    @DisplayName("게시글을 등록할 때 회원 ID로 회원이 존재하지 않으면 예외가 발생한다.")
    @Test
    void addPostNotExistsMemberId() throws Exception {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(1L)
                .title("글 제목")
                .content("글 내용")
                .build();

        BDDMockito.given(postService.addPost(anyLong(), any(CreatePostServiceRequest.class)))
                .willThrow(new MemberNotFoundException());

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."))
        ;
    }

    @CustomMockRoleUser
    @DisplayName("게시글을 등록할 때 카테고리 ID로 카테고리가 존재하지 않으면 예외가 발생한다.")
    @Test
    void addPostNotExistsCategoryId() throws Exception {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .categoryId(1L)
                .title("글 제목")
                .content("글 내용")
                .build();

        BDDMockito.given(postService.addPost(anyLong(), any(CreatePostServiceRequest.class)))
                .willThrow(new CategoryNotFoundException());

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("카테고리를 찾을 수 없습니다."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글을 제거한다.")
    @Test
    void removePost() throws Exception {
        // when
        mockMvc.perform(delete("/api/v1/posts/{postId}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글을 제거할 떄 게시글이 존재하지 않으면 예외가 발생한다.")
    @Test
    void removePostNotExistsPost() throws Exception {
        // given
        BDDMockito.willThrow(new PostNotFoundException()).given(postService).removePost(anyLong());

        // when
        mockMvc.perform(delete("/api/v1/posts/{postId}", 1L)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글을 수정한다.")
    @Test
    void updatePost() throws Exception {
        // given
        EditPostRequest request = EditPostRequest.builder()
                .categoryId(1L)
                .title("글 작성")
                .content("내용입니다.")
                .postStatus(PostStatus.PUBLIC)
                .build();

        BDDMockito.willDoNothing()
                .given(postService).editPost(anyLong(), any(EditPostServiceRequest.class));

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;

        BDDMockito.then(postService).should().editPost(anyLong(), any(EditPostServiceRequest.class));
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글을 수정할 때 카테고리는 필수값이다.")
    @Test
    void updatePostWithoutCategory() throws Exception {
        // given
        EditPostRequest request = EditPostRequest.builder()
                .categoryId(null)
                .title("글 작성")
                .content("내용입니다.")
                .postStatus(PostStatus.PUBLIC)
                .build();

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.categoryId").value("카테고리는 필수입니다."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글을 수정할 때 게시글 상태는 필수값이다.")
    @Test
    void updatePostWithoutPostStatus() throws Exception {
        // given
        EditPostRequest request = EditPostRequest.builder()
                .categoryId(1L)
                .title("글 작성")
                .content("내용입니다.")
                .postStatus(null)
                .build();

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.postStatus").value("게시글 상태는 필수입니다."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글을 수정할 때 게시글이 존재하지 않으면 예외가 발생한다.")
    @Test
    void updatePostNotExistsPost() throws Exception {
        // given
        EditPostRequest request = EditPostRequest.builder()
                .categoryId(1L)
                .title("글 작성")
                .content("내용입니다.")
                .postStatus(PostStatus.PUBLIC)
                .build();

        BDDMockito.willThrow(new PostNotFoundException())
                        .given(postService).editPost(anyLong(), any(EditPostServiceRequest.class));

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("게시글을 수정할 때 카테고리가 존재하지 않으면 예외가 발생한다.")
    @Test
    void updatePostNotExistsCategory() throws Exception {
        // given
        EditPostRequest request = EditPostRequest.builder()
                .categoryId(1L)
                .title("글 작성")
                .content("내용입니다.")
                .postStatus(PostStatus.PUBLIC)
                .build();

        BDDMockito.willThrow(new CategoryNotFoundException())
                .given(postService).editPost(anyLong(), any(EditPostServiceRequest.class));

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("카테고리를 찾을 수 없습니다."))
        ;
    }

    @DisplayName("게시글을 단건 조회한다.")
    @Test
    void getPost() throws Exception {
        // given
        LocalDateTime currTime = LocalDateTime.now();
        PostDetailResponse response = PostDetailResponse.builder()
                .memberId(1L)
                .memberName("호랑이")
                .categoryId(1L)
                .categoryName("날씨")
                .id(1L)
                .title("무더위")
                .content("날씨가 덥습니다.")
                .views(0)
                .regDate(currTime)
                .build();

        BDDMockito.given(postService.getPost(anyLong()))
                .willReturn(response);

        // when
        mockMvc.perform(get("/api/v1/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.memberName").value("호랑이"))
                .andExpect(jsonPath("$.data.categoryId").value(1L))
                .andExpect(jsonPath("$.data.categoryName").value("날씨"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("무더위"))
                .andExpect(jsonPath("$.data.content").value("날씨가 덥습니다."))
                .andExpect(jsonPath("$.data.views").value(0))
                .andExpect(jsonPath("$.data.regDate").value(currTime.toString()))
        ;

        BDDMockito.then(postService).should().getPost(anyLong());
    }

    @DisplayName("게시글을 단건 조회할 때 게시글이 존재하지 않으면 예외가 발생한다.")
    @Test
    void getPostNotExistsPost() throws Exception {
        // given
        BDDMockito.given(postService.getPost(anyLong()))
                .willThrow(new PostNotFoundException());

        // when
        mockMvc.perform(get("/api/v1/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
        ;
    }

    private PostResponse createPostResponse(LocalDateTime currentTime, Long postId, String title, String content) {
        return PostResponse.builder()
                .id(postId)
                .title(title)
                .content(content)
                .regDate(currentTime)
                .categoryId(1L)
                .categoryName("기타1")
                .build();
    }
}