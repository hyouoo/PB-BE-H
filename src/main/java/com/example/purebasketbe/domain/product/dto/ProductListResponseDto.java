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
    private List<ImageResponseDto> eventImageUrls;
    private Page<ProductResponseDto> products;
    private List<ImageResponseDto> imageUrls;


    @Builder
    private ProductListResponseDto(Page<ProductResponseDto> eventProducts,
                                   List<ImageResponseDto> eventImageUrls,
                                   Page<ProductResponseDto> products,
                                   List<ImageResponseDto> imageUrls) {
        this.eventProducts = eventProducts;
        this.eventImageUrls = eventImageUrls;
        this.products = products;
        this.imageUrls = imageUrls;
    }

    public static ProductListResponseDto of(Page<ProductResponseDto> eventProducts,
                                            List<ImageResponseDto> eventImageUrls,
                                            Page<ProductResponseDto> products,
                                            List<ImageResponseDto> imageUrls) {
        return ProductListResponseDto.builder()
                .eventProducts(eventProducts)
                .eventImageUrls(eventImageUrls)
                .products(products)
                .imageUrls(imageUrls)
                .build();
    }
}
