package com.replyboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replyboard.api.controller.auth.AuthController;
import com.replyboard.api.controller.category.CategoryController;
import com.replyboard.api.controller.comment.CommentController;
import com.replyboard.api.controller.member.MemberController;
import com.replyboard.api.controller.post.PostController;
import com.replyboard.api.service.auth.AuthService;
import com.replyboard.api.service.category.CategoryService;
import com.replyboard.api.service.comment.CommentService;
import com.replyboard.api.service.member.MemberService;
import com.replyboard.api.service.post.PostService;
import com.replyboard.config.TestSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
        AuthController.class,
        CategoryController.class,
        PostController.class,
        CommentController.class,
        MemberController.class
})
@Import(TestSecurityConfig.class)
public class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected PostService postService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected MemberService memberService;
}
