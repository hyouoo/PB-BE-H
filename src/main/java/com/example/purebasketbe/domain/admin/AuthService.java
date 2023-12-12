package com.example.purebasketbe.domain.admin;

import com.example.purebasketbe.domain.member.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;

    public Authentication authenticateAdmin(LoginRequestDto requestDto) {
        // 이메일과 비밀번호를 사용하여 UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password());
        // AuthenticationManager를 사용하여 인증 시도 후 인증 성공 시, Authentication 객체 반환
        return authenticationManager.authenticate(authenticationToken);
    }
}
