package com.replyboard.api.security.auth;

import com.replyboard.api.dto.MemberDto;
import com.replyboard.domain.member.Member;
import com.replyboard.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원정보가 일치하지 않습니다."));

        MemberDto memberDto = MemberDto.of(member);

        return new CustomUserDetails(memberDto);
    }
}
