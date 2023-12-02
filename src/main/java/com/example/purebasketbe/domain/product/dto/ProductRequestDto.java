package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRequestDto {

    @NotBlank(message = "상품명을 입력해 주세요.")
    private String name;

    @Min(value = 0, message = "가격을 0이상으로 입력해 주세요.")
    private int price;

    @Min(value = 0, message = "재고를 0이상으로 입력해 주세요.")
    private int quantity;

    private String info;

    private String category;

    @Enumerated(EnumType.STRING)
    private Event event;

}
