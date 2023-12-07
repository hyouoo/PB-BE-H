package com.example.purebasketbe.domain.email;

import lombok.Getter;

@Getter
public class EmailVerificationResult {
    private final boolean successful;
    private final String message;

    private EmailVerificationResult(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }

    public static EmailVerificationResult of(boolean successful) {
        return new EmailVerificationResult(successful, successful ? "인증 성공" : "인증 실패");
    }
}
