package com.replyboard.api.controller.comment;

import com.replyboard.ControllerTestSupport;
import com.replyboard.api.controller.comment.request.CreateCommentRequest;
import com.replyboard.api.controller.comment.request.EditCommentRequest;
import com.replyboard.api.controller.comment.request.RemoveCommentRequest;
import com.replyboard.api.service.comment.request.CreateCommentServiceRequest;
import com.replyboard.api.service.comment.request.EditCommentServiceRequest;
import com.replyboard.api.service.comment.request.RemoveCommentServiceRequest;
import com.replyboard.api.service.comment.response.CommentResponse;
import com.replyboard.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends ControllerTestSupport {

    @DisplayName("댓글 전체 목록을 조회한다")
    @Test
    void getCommentList() throws Exception {
        // given
        CommentResponse reply1 = createComment(
                2L, 1L, "파도", "1234", "저도 공감합니다.", null);

        CommentResponse reply2 = createComment(
                3L, 1L, "리플러", "1234", "공감합니다.", null);

        List<CommentResponse> replies = List.of(reply1, reply2);

        CommentResponse commentResponse = createComment(
                1L, null, "노을", "1234", "유용한 정보입니다.", replies);

        BDDMockito.given(commentService.getCommentList(anyLong()))
                .willReturn(List.of(commentResponse));

        // when
        mockMvc.perform(get("/api/v1/posts/{postId}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].parentId").isEmpty())
                .andExpect(jsonPath("$.data[0].author").value("노을"))
                .andExpect(jsonPath("$.data[0].password").value("1234"))
                .andExpect(jsonPath("$.data[0].content").value("유용한 정보입니다."))
                .andExpect(jsonPath("$.data[0].replies.length()").value(2))
                .andExpect(jsonPath("$.data[0].replies[0].id").value(2L))
                .andExpect(jsonPath("$.data[0].replies[0].parentId").value(1L))
                .andExpect(jsonPath("$.data[0].replies[0].author").value("파도"))
                .andExpect(jsonPath("$.data[0].replies[0].password").value("1234"))
                .andExpect(jsonPath("$.data[0].replies[0].content").value("저도 공감합니다."))
                .andExpect(jsonPath("$.data[0].replies[0].replies").isEmpty())
                .andExpect(jsonPath("$.data[0].replies[1].id").value(3L))
                .andExpect(jsonPath("$.data[0].replies[1].parentId").value(1L))
                .andExpect(jsonPath("$.data[0].replies[1].author").value("리플러"))
                .andExpect(jsonPath("$.data[0].replies[1].password").value("1234"))
                .andExpect(jsonPath("$.data[0].replies[1].content").value("공감합니다."))
                .andExpect(jsonPath("$.data[0].replies[1].replies").isEmpty())
                ;
    }

    @DisplayName("댓글 전체 목록을 조회할 때 댓글이 하나도 없으면 data는 빈 배열이다.")
    @Test
    void getCommentListIsEmpty() throws Exception {
        // given
        BDDMockito.given(commentService.getCommentList(anyLong()))
                .willReturn(List.of());

        // when
        mockMvc.perform(get("/api/v1/posts/{postId}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    private static CommentResponse createComment(
            long id, Long parentId, String author, String password, String content, List<CommentResponse> replies
    ) {
        return CommentResponse.builder()
                .id(id)
                .parentId(parentId)
                .author(author)
                .password(password)
                .content(content)
                .replies(replies)
                .build();
    }

    @DisplayName("댓글을 등록한다.")
    @Test
    void addComment() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content("유용한 정보 감사합니다.")
                .build();

        BDDMockito.given(commentService.addComment(anyLong(), any(CreateCommentServiceRequest.class)))
                .willReturn(1L);

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(1L))
        ;

        // then
        BDDMockito.then(commentService).should().addComment(anyLong(), any(CreateCommentServiceRequest.class));
    }

    @DisplayName("댓글을 등록할 때 작성자명은 필수다.")
    @Test
    void addCommentWithoutAuthor() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author(null)
                .password("1234")
                .content("유용한 정보 감사합니다.")
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.author").value("작성자명을 입력해주세요."))
        ;
    }

    @DisplayName("댓글을 등록할 때 비밀번호는 필수다.")
    @Test
    void addCommentWithoutPassword() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password(null)
                .content("유용한 정보 감사합니다.")
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요."))
        ;
    }

    @DisplayName("댓글을 등록할 때 댓글 내용은 필수다.")
    @Test
    void addCommentWithoutContent() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content(null)
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.content").value("댓글 내용을 입력해주세요."))
        ;
    }

    @DisplayName("댓글을 등록할 때 게시글이 존재하지 않으면 예외가 발생한다.")
    @Test
    void addCommentNotExistsPost() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content("좋은 글입니다.")
                .build();

        BDDMockito.given(commentService.addComment(anyLong(), any(CreateCommentServiceRequest.class)))
                .willThrow(new PostNotFoundException());

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
        ;
    }

    @DisplayName("댓글에 대댓글을 등록한다.")
    @Test
    void addReply() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content("유용한 정보 감사합니다.")
                .build();

        BDDMockito.given(commentService.addReply(anyLong(), anyLong(), any(CreateCommentServiceRequest.class)))
                .willReturn(1L);

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/replies", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(1L))
        ;

        // then
        BDDMockito.then(commentService).should().addReply(anyLong(), anyLong(), any(CreateCommentServiceRequest.class));
    }

    @DisplayName("댓글에 대댓글을 등록할 때 작성자는 필수값이다.")
    @Test
    void addReplyWithoutAuthor() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author(null)
                .password("1234")
                .content("유용한 정보 감사합니다.")
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/replies", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.author").value("작성자명을 입력해주세요."))
        ;
    }

    @DisplayName("댓글에 대댓글을 등록할 때 비밀번호는 필수값이다.")
    @Test
    void addReplyWithoutPassword() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password(null)
                .content("유용한 정보 감사합니다.")
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/replies", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요."))
        ;
    }

    @DisplayName("댓글에 대댓글을 등록할 때 댓글 내용은 필수값이다.")
    @Test
    void addReplyWithoutContent() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content(null)
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/replies", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.content").value("댓글 내용을 입력해주세요."))
        ;
    }

    @DisplayName("댓글에 대댓글을 등록할 때 게시글이 없으면 예외가 발생한다.")
    @Test
    void addReplyNotExistsPost() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content("유용한 정보 감사합니다.")
                .build();

        BDDMockito.given(commentService.addReply(anyLong(), anyLong(), any(CreateCommentServiceRequest.class)))
                .willThrow(new PostNotFoundException());

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/replies", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
        ;
    }

    @DisplayName("댓글에 대댓글을 등록할 때 부모 댓글이 없으면 예외가 발생한다.")
    @Test
    void addReplyNotExistsComment() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content("유용한 정보 감사합니다.")
                .build();

        BDDMockito.given(commentService.addReply(anyLong(), anyLong(), any(CreateCommentServiceRequest.class)))
                .willThrow(new CommentNotFoundException());

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/replies", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("댓글을 찾을 수 없습니다."))
        ;
    }

    @DisplayName("댓글에 대댓글을 등록할 때 대댓글에 또다른 댓글을 달 수 없다.")
    @Test
    void addReplyAnotherReply() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .author("노을")
                .password("1234")
                .content("유용한 정보 감사합니다.")
                .build();

        BDDMockito.given(commentService.addReply(anyLong(), anyLong(), any(CreateCommentServiceRequest.class)))
                .willThrow(new CanNotAnotherReplyException());

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/replies", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("답글에 댓글을 달 수 없습니다."))
        ;
    }

    @DisplayName("댓글을 삭제한다")
    @Test
    void removeComment() throws Exception {
        // given
        RemoveCommentRequest request = RemoveCommentRequest.builder()
                .password("1234")
                .build();

        BDDMockito.willDoNothing()
                .given(commentService).removeComment(anyLong(), anyLong(), any(RemoveCommentServiceRequest.class));

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/delete", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;

        BDDMockito.then(commentService).should().removeComment(anyLong(), anyLong(), any(RemoveCommentServiceRequest.class));
    }

    @DisplayName("댓글을 삭제할 때 비밀번호는 필수값이다.")
    @Test
    void removeCommentWithoutPassword() throws Exception {
        // given
        RemoveCommentRequest request = RemoveCommentRequest.builder()
                .password(null)
                .build();

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/delete", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요."))
        ;
    }

    @DisplayName("댓글을 삭제할 때 댓글이 작성된 게시글이 일치하지 않으면 예외가 발생한다.")
    @Test
    void removeCommentNotSamePost() throws Exception {
        // given
        RemoveCommentRequest request = RemoveCommentRequest.builder()
                .password("1234")
                .build();

        BDDMockito.willThrow(new NotSamePostException())
                        .given(commentService).removeComment(anyLong(), anyLong(), any(RemoveCommentServiceRequest.class));

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/delete", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("댓글이 작성된 게시글과 일치하지 않습니다."))
        ;
    }

    @DisplayName("댓글을 삭제할 때 댓글이 비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void removeCommentNotMatchPassword() throws Exception {
        // given
        RemoveCommentRequest request = RemoveCommentRequest.builder()
                .password("1234")
                .build();

        BDDMockito.willThrow(new NotMatchPasswordException())
                .given(commentService).removeComment(anyLong(), anyLong(), any(RemoveCommentServiceRequest.class));

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}/delete", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
        ;
    }

    @DisplayName("댓글을 수정한다.")
    @Test
    void editComment() throws Exception {
        // given
        EditCommentRequest request = EditCommentRequest.builder()
                .password("1234")
                .content("글을 수정하기")
                .build();

        BDDMockito.willDoNothing()
                .given(commentService).editComment(anyLong(), anyLong(), any(EditCommentServiceRequest.class));

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;

        BDDMockito.then(commentService).should().editComment(anyLong(), anyLong(), any(EditCommentServiceRequest.class));
    }

    @DisplayName("댓글을 수정할 때 비밀번호는 필수값이다.")
    @Test
    void editCommentWithoutPassword() throws Exception {
        // given
        EditCommentRequest request = EditCommentRequest.builder()
                .password(null)
                .content("글을 수정하기")
                .build();

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요."))
        ;
    }

    @DisplayName("댓글을 수정할 때 비밀번호는 4~16자 사이로 입력해야 한다.")
    @Test
    void editCommentPasswordLengthFourToSixteen() throws Exception {
        // given
        EditCommentRequest request = EditCommentRequest.builder()
                .password("123")
                .content("글을 수정하기")
                .build();

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호는 4~16자 사이로 입력해주세요."))
        ;
    }

    @DisplayName("댓글을 수정할 때 댓글 내용은 필수값이다.")
    @Test
    void editCommentWithoutContent() throws Exception {
        // given
        EditCommentRequest request = EditCommentRequest.builder()
                .password("1234")
                .content(null)
                .build();

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.content").value("댓글 내용을 입력해주세요."))
        ;
    }

    @DisplayName("댓글을 수정할 때 댓글이 작성된 게시글이 일치하지 않으면 예외가 발생한다.")
    @Test
    void editCommentNotSamePost() throws Exception {
        // given
        EditCommentRequest request = EditCommentRequest.builder()
                .password("1234")
                .content("글 수정하기")
                .build();

        BDDMockito.willThrow(new NotSamePostException())
                .given(commentService).editComment(anyLong(), anyLong(), any(EditCommentServiceRequest.class));

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("댓글이 작성된 게시글과 일치하지 않습니다."))
        ;
    }

    @DisplayName("댓글을 수정할 때 댓글이 비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void editCommentNotMatchPassword() throws Exception {
        // given
        EditCommentRequest request = EditCommentRequest.builder()
                .password("1234")
                .content("글 수정하기")
                .build();

        BDDMockito.willThrow(new NotMatchPasswordException())
                .given(commentService).editComment(anyLong(), anyLong(), any(EditCommentServiceRequest.class));

        // when
        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
        ;
    }
}