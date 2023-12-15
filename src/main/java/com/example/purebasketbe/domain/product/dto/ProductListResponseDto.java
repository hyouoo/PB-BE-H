package com.example.purebasketbe.domain.product.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;


public record ProductListResponseDto(Page<ProductResponseDto> eventProducts, Page<ProductResponseDto> products) {

    @Builder
    public ProductListResponseDto {

    }

    public static ProductListResponseDto of(Page<ProductResponseDto> eventProducts, Page<ProductResponseDto> products) {
        return ProductListResponseDto.builder()
                .eventProducts(eventProducts)
                .products(products)
                .build();
    }
}
