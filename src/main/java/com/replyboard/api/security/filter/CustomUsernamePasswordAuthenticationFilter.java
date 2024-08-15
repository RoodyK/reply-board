package com.replyboard.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public CustomUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper, String loginUrl) {
        this.objectMapper = objectMapper;
        setFilterProcessesUrl(loginUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

            setDetails(request, token);
            return this.getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        String sessionId = request.getSession().getId();
        LocalDateTime sessionExpiryTime = LocalDateTime.now().plusSeconds(request.getSession().getMaxInactiveInterval());

        log.info("[로그인 성공] 세션 아이디 : {}", sessionId);
        log.info("[로그인 성공] 세션 만료시간 : {}", sessionExpiryTime);

        super.successfulAuthentication(request, response, chain, authentication);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginDto {

        private String username;
        private String password;
    }
}
