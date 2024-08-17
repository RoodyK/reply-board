package com.replyboard.api.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replyboard.api.dto.ApiDataResponse;
import com.replyboard.api.dto.MemberDto;
import com.replyboard.api.security.auth.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        MemberDto memberDto = userDetails.getMemberDto();
        log.info("memberDto = {}", memberDto);

        String sessionId = request.getSession().getId();
        LocalDateTime sessionExpiryTime = LocalDateTime.now().plusSeconds(request.getSession().getMaxInactiveInterval());

        log.info("[로그인 성공] 세션 아이디 : {}", sessionId);
        log.info("[로그인 성공] 세션 만료시간 : {}", sessionExpiryTime);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), ApiDataResponse.empty());
    }
}
