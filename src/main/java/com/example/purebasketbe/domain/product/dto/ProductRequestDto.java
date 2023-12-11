package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequestDto(

        @NotBlank(message = "상품명을 입력해 주세요.")
        String name,

        @PositiveOrZero(message = "가격을 0이상으로 입력해 주세요.")
        Integer price,

        @PositiveOrZero(message = "재고를 0이상으로 입력해 주세요.")
        Integer stock,

        String info,

        String category,

        Event event,

        @Min(value = 0, message = "할인율은 0 이상으로 입력해 주세요.")
        @Max(value = 100, message = "할인율은 100 이하로 입력해 주세요")
        Integer discountRate
) {
}