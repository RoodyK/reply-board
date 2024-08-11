package com.replyboard.api.controller.auth;

import com.replyboard.api.controller.auth.request.SignupRequest;
import com.replyboard.api.dto.ApiDataResponse;
import com.replyboard.api.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.url-prefix}")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiDataResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.empty());
    }
}
