package com.example.purebasketbe.domain.admin;

import com.example.purebasketbe.domain.member.dto.LoginRequestDto;
import com.example.purebasketbe.domain.member.entity.UserRole;
import com.example.purebasketbe.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<Void> adminLogin(@RequestBody LoginRequestDto requestDto) {
        // 관리자 인증 서비스로 로그인 처리
        Authentication authentication = authService.authenticateAdmin(requestDto);
        // JWT 토큰 생성
        String token = jwtUtil.createToken(authentication.getName(), UserRole.ADMIN);
        // 토큰을 응답 헤더에 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, token);
        // 성공 메시지와 함께 응답 반환
        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }
}
