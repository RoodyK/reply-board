package com.replyboard.api.service.auth;

import com.replyboard.api.service.auth.request.SignupServiceRequest;
import com.replyboard.domain.member.MemberRepository;
import com.replyboard.exception.DuplicatedMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupServiceRequest serviceRequest) {
        serviceRequest.validation();

        if (memberRepository.findByEmail(serviceRequest.getEmail()).isPresent()) {
            throw new DuplicatedMemberException();
        }

        String encodedPassword = passwordEncoder.encode(serviceRequest.getPassword());

        memberRepository.save(serviceRequest.toEntity(encodedPassword));
    }
}
