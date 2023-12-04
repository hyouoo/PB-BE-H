package com.example.purebasketbe.domain.product.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListResponseDto {

    private List<ProductResponseDto> eventProducts;
    private List<ProductResponseDto> products;


    @Builder
    private ProductListResponseDto(List<ProductResponseDto> eventProducts,
                                   List<ProductResponseDto> products) {
        this.eventProducts = eventProducts;
        this.products = products;
    }

    public static ProductListResponseDto of(List<ProductResponseDto> eventProducts,
                                            List<ProductResponseDto> products) {
        return ProductListResponseDto.builder()
                .eventProducts(eventProducts)
                .products(products)
                .build();
    }
}
