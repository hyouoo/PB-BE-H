package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRequestDto {

    @NotBlank(message = "상품명을 입력해 주세요.")
    private String name;

<<<<<<< HEAD
    @PositiveOrZero(message = "가격을 0이상으로 입력해 주세요.")
    private Integer price;

    @PositiveOrZero(message = "재고를 0이상으로 입력해 주세요.")
=======
    @Min(value = 0, message = "가격을 0이상으로 입력해 주세요.")
    private Integer price;

    @Min(value = 0, message = "재고를 0이상으로 입력해 주세요.")
>>>>>>> edce372 (Fix: Rebase Conflict 해결)
    private Integer stock;

    private String info;

    private String category;

    private Integer discountRate;

    @Enumerated(EnumType.STRING)
    private Event event;

    @Min(value = 0, message = "할인율은 0 이상으로 입력해 주세요.")
    @Max(value = 100, message = "할인율은 100 이하로 입력해 주세요")
    private Integer discountRate;
}
