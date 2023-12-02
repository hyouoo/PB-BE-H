package com.example.purebasketbe.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequestDto {

    private String email;

    private String password;

    private String address;

    private String phone;

    private boolean deleted;
}
