package com.example.purebasketbe.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    MEMBER(Authority.MEMBER),
    OWNER(Authority.ADMIN);

    private final String authority;

    public static class Authority {
        public static final String MEMBER = "ROLE_MEMBER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
