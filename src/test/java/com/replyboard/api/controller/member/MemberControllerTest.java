package com.replyboard.api.controller.member;

import com.replyboard.ControllerTestSupport;
import com.replyboard.api.service.member.response.MemberResponse;
import com.replyboard.config.admin.CustomMockRoleAdmin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestSupport {

    @CustomMockRoleAdmin
    @DisplayName("회원 정보를 조회한다.")
    @Test
    void getMemberProfile() throws Exception {
        // given
        MemberResponse memberResponse = MemberResponse.builder()
                .id(1L)
                .name("루디")
                .build();

        BDDMockito.given(memberService.getProfile(anyLong()))
                .willReturn(memberResponse);

        // when
        mockMvc.perform(get("/api/v1/members/profile")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("루디"))
        ;

        // then
        BDDMockito.then(memberService).should().getProfile(anyLong());
    }

    @DisplayName("로그인하지 않은 사용자가 회원 정보를 조회하면 data는 null이다.")
    @Test
    void getMemberProfileNotLoginUser() throws Exception {
        // when
        mockMvc.perform(get("/api/v1/members/profile")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }
}