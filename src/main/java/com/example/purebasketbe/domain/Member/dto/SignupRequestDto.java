package com.example.purebasketbe.domain.Member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequestDto (
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String address,
        @NotBlank String phone,
        boolean deleted
){ }
