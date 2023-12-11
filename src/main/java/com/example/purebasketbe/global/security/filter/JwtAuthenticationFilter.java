package com.example.purebasketbe.global.security.filter;

import com.example.purebasketbe.domain.member.MemberService;
import com.example.purebasketbe.domain.member.dto.LoginRequestDto;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.member.entity.UserRole;
import com.example.purebasketbe.global.redis.RedisService;
import com.example.purebasketbe.global.security.impl.UserDetailsImpl;
import com.example.purebasketbe.global.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisService redisService, MemberService memberService) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.memberService = memberService;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        log.info("로그인 시도");

        try {
            LoginRequestDto requestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            log.info("Email: {}", requestDto.email());

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.email(),
                            requestDto.password(),
                            null
                    )
            );

        } catch (IOException | AuthenticationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException {

        log.info("로그인 성공 및 JWT 생성");

        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRole role = ((UserDetailsImpl) authResult.getPrincipal()).getMember().getRole();



        String token = jwtUtil.createToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email);
        jwtUtil.addJwtToHeader(JwtUtil.AUTHORIZATION_HEADER, token, response);
        jwtUtil.addJwtToHeader(JwtUtil.REFRESHTOKEN_HEADER,refreshToken,response);

        // 로그인 성공시 Refresh Token Redis 저장 ( key = Email / value = Refresh Token )
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        Member findMember = memberService.findMemberByEmail(userDetails.getUsername());
        long refreshTokenExpirationMillis = jwtUtil.getRefreshTokenExpirationMillis();
        redisService.setValues(findMember.getEmail(), refreshToken, Duration.ofMillis(refreshTokenExpirationMillis));

        // 성공 메시지와 토큰을 JSON 형식으로 응답에 추가
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse("로그인 성공")));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        log.info("로그인 실패: {}", failed.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse("로그인 실패")));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // 간단한 응답 객체
    @Getter
    private static class SimpleResponse {
        private final String message;

        public SimpleResponse(String message) {
            this.message = message;
        }
    }
}