package com.example.purebasketbe.domain.member;

import com.example.purebasketbe.domain.email.EmailVerificationResult;
import com.example.purebasketbe.domain.member.dto.SignupRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> registerMember(@RequestBody SignupRequestDto requestDto) {
        memberService.registerMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/emails/verification-requests")
    public ResponseEntity<Void> sendMessage(@RequestParam("email") @Valid @CustomEmail String email) {
        memberService.sendCodeToEmail(email);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/emails/verifications")
    public ResponseEntity<String> verificationEmail(@RequestParam("email") @Valid @CustomEmail String email,
                                                    @RequestParam("code") String authCode) {
        EmailVerificationResult result = memberService.verifiedCode(email, authCode);

        if (result.isSuccessful()) {
            return ResponseEntity.ok("이메일 인증에 성공했습니다");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 인증에 실패했습니다");
        }
    }
}
