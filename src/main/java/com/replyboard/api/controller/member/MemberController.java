package com.replyboard.api.controller.member;

import com.replyboard.api.dto.ApiDataResponse;
import com.replyboard.api.dto.MemberDto;
import com.replyboard.api.security.auth.CustomUserDetails;
import com.replyboard.api.service.member.MemberService;
import com.replyboard.api.service.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.url-prefix}")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/profile")
    public ResponseEntity<ApiDataResponse<MemberResponse>> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok().body(ApiDataResponse.empty());
        }

        MemberDto memberDto = userDetails.getMemberDto();
        MemberResponse response = memberService.getProfile(memberDto.getId());

        return ResponseEntity.ok().body(ApiDataResponse.of(response));
    }
}
