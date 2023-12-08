package com.example.purebasketbe.domain.cart.dto;

import jakarta.validation.constraints.Min;

public record CartRequestDto(

        @Min(value = 1, message = "1이상의 값을 입력해 주세요.")
        int amount
) {
}