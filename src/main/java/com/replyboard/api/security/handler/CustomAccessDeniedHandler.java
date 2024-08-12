package com.replyboard.api.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replyboard.api.dto.ApiErrorResponse;
import com.replyboard.constant.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("[인가되지 않는 사용자의 접근]", accessDeniedException);

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(false, ResultCode.FORBIDDEN, "페이지에 접근할 수 없습니다.");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        objectMapper.writeValue(response.getWriter(), apiErrorResponse);
    }
}
