package com.example.purebasketbe.domain.member;

import com.example.purebasketbe.domain.member.dto.SignupRequestDto;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MemberService memberService;

    @Nested
    @DisplayName("회원가입")
    class Register {


        private SignupRequestDto requestDto;

        @BeforeEach
        void setUp() {
            requestDto = new SignupRequestDto(
                    "test name",
                    "test mail",
                    "testpassword",
                    "test address",
                    "01012345678"
            );
        }

        @Test
        @DisplayName("회원가입 성공")

        void RegisterSuccess() {
            // given
            given(passwordEncoder.encode(requestDto.password())).willReturn("encoded");
            given(memberRepository.existsByEmail(requestDto.email())).willReturn(false);

            // when
            memberService.registerMember(requestDto);

            // then
            verify(memberRepository).save(any(Member.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
        void RegisterFail() {
            // given
            given(passwordEncoder.encode(requestDto.password())).willReturn("encoded");
            given(memberRepository.existsByEmail(requestDto.email())).willReturn(true);

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> memberService.registerMember(requestDto)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }
    }

    @Nested
    @DisplayName("이메일 확인")
    class FindEmail {
        @Test
        @DisplayName("이메일 확인 성공")
        void findMemberByEmailSuccess() {
            // given
            String email = "test@gmail.com";
            Member member = Member.builder().email(email).build();
            given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

            // when
            Member result = memberService.findMemberByEmail(email);

            // then
            assertThat(result.getEmail()).isEqualTo(email);
        }
        @Test
        @DisplayName("이메일 확인 실패")
        void findMemberByEmailFail() {
            // given
            String email = "test@gmail.com";

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> memberService.findMemberByEmail(email)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.EMAIL_NOT_FOUND.getMessage());
        }
    }

}