package com.example.purebasketbe.domain.admin;

import com.example.purebasketbe.domain.member.dto.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthService authService;


    private LoginRequestDto requestDto;
    @BeforeEach
    void setUp() {
        requestDto = new LoginRequestDto("test@gmail.com", "password");
    }

    @Test
    @DisplayName("관리자 로그인 성공")
    void authenticationAdminSuccess() {
        // given
        Authentication authentication = mock(Authentication.class);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password());
        given(authenticationManager.authenticate(authenticationToken)).willReturn(authentication);

        // when
        Authentication result = authService.authenticateAdmin(requestDto);

        // then
        assertThat(result).isNotNull();

    }
    @Test
    @DisplayName("관리자 로그인 실패")
    void authenticationAdminFail() {
        // given
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password());
        given(authenticationManager.authenticate(authenticationToken)).willThrow(RuntimeException.class);

        // when -  then
        assertThrows(RuntimeException.class,
                () -> authService.authenticateAdmin(requestDto)
        );
    }
}
