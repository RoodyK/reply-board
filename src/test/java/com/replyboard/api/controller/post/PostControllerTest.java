package com.replyboard.api.controller.post;

import com.replyboard.api.controller.post.request.PostSearch;
import com.replyboard.api.dto.PagingResponse;
import com.replyboard.api.service.post.PostService;
import com.replyboard.api.service.post.response.PostResponse;
import com.replyboard.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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