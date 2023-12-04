package com.example.purebasketbe.domain.Member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequestDto (
        @NotBlank(message = "이름을 입력해주세요")
        String name,
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "형식에 맞게 입력해주세요")
        String email,
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
                message = "비밀번호는 8~15자리면서 알파벳, 숫자, 특수문자를 포함해야합니다")
        String password,
        @NotBlank(message = "정확한 주소를 입력해주세요")
        String address,
        @NotBlank(message = "전화번호를 입력해주세요")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 10~11자리의 숫자이어야 합니다")
        String phone,
        boolean deleted
){ }
