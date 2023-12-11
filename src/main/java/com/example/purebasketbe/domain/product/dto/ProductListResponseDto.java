package com.example.purebasketbe.domain.product.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListResponseDto {

    private Page<ProductResponseDto> eventProducts;
    private Page<ProductResponseDto> products;


    @Builder
    private ProductListResponseDto(Page<ProductResponseDto> eventProducts,
                                   Page<ProductResponseDto> products) {
        this.eventProducts = eventProducts;
        this.products = products;
    }

    public static ProductListResponseDto of(Page<ProductResponseDto> eventProducts,
                                            Page<ProductResponseDto> products) {
        return ProductListResponseDto.builder()
                .eventProducts(eventProducts)
                .products(products)
                .build();
    }
}
