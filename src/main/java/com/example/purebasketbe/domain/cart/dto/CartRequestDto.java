package com.example.purebasketbe.domain.cart.dto;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequestDto {

    @Min(value = 0, message = "0이상의 값을 입력해 주세요.")
    private int amount;
}