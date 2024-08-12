package com.replyboard.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replyboard.api.dto.ApiErrorResponse;
import com.replyboard.constant.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@TestConfiguration
@EnableMethodSecurity
@RequiredArgsConstructor
public class TestSecurityConfig {

    private final ObjectMapper objectMapper;

    @Bean
    @Order(10)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/favicon.ico", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
        ;

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/categories").hasRole("ADMIN")
                        .requestMatchers("/api/v1/categories/{categoryId}").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(false, ResultCode.FORBIDDEN, "페이지에 접근할 수 없습니다.");
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                            objectMapper.writeValue(response.getWriter(), apiErrorResponse);
                        })
                )
        ;

        return http.build();
    }
}
