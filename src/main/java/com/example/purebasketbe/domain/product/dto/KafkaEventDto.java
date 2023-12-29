package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Product;
import lombok.Builder;

public record KafkaEventDto(
        String name,
        int price,
        int stock,
        int discountRate
) {

    @Builder
    public KafkaEventDto {
    }


    public static KafkaEventDto from(Product product) {
        return KafkaEventDto.builder()
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .discountRate(product.getDiscountRate())
                .build();
    }
}
