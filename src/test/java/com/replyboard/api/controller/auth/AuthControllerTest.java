package com.replyboard.api.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replyboard.config.TestSecurityConfig;
import com.replyboard.api.controller.auth.request.SignupRequest;
import com.replyboard.api.service.auth.AuthService;
import com.replyboard.api.service.auth.request.SignupServiceRequest;
import com.replyboard.exception.DuplicatedMemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @DisplayName("회원 가입 성공")
    @Test
    void signup() throws Exception {
        // given
        SignupRequest request = getSignupRequest("pps8853@gmail.com", "1234", "루디");

        BDDMockito.willDoNothing().given(authService).signup(any(SignupServiceRequest.class));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
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

        // then
        BDDMockito.then(authService).should(times(1)).signup(any(SignupServiceRequest.class));
    }

    @DisplayName("회원 가입 시 이메일은 필수값이다.")
    @Test
    void signupWithoutEmail() throws Exception {
        // given
        SignupRequest request = getSignupRequest(null, "1234", "루디");

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.email").value("이메일을 입력해주세요"))
        ;
    }

    @DisplayName("회원 가입 시 비밀번호는 필수값이다.")
    @Test
    void signupWithoutPassword() throws Exception {
        // given
        SignupRequest request = getSignupRequest("pps8853@gmail.com", null, "루디");

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요."))
        ;
    }

    @DisplayName("회원 가입 시 비밀번호는 4~20자 사이로 입력해야 한다.")
    @Test
    void signupForPasswordLengthFourToTwenty() throws Exception {
        // given
        SignupRequest request = getSignupRequest("pps8853@gmail.com", "123", "루디");

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호는 4~20자 사이로 입력해주세요."))
        ;
    }

    @DisplayName("회원 가입 시 이름값은 필수값이다.")
    @Test
    void signupWithoutName() throws Exception {
        // given
        SignupRequest request = getSignupRequest("pps8853@gmail.com", "12345", null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.name").value("이름을 입력해주세요."))
        ;
    }

    @DisplayName("회원 가입 시 이메일이 중복되면 예외가 발생한다.")
    @Test
    void signupDuplicatedEmail() throws Exception {
        // given
        SignupRequest request = getSignupRequest("pps8853@gmail.com", "12345", "루디");

        BDDMockito.willThrow(new DuplicatedMemberException()).given(authService).signup(any(SignupServiceRequest.class));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(5000))
                .andExpect(jsonPath("$.message").value("중복된 회원이 존재합니다."))
                .andExpect(jsonPath("$.validation").isEmpty())
        ;
    }

    private SignupRequest getSignupRequest(String email, String password, String name) {
        return SignupRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }
}