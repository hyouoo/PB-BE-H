package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductResponseDto(
        Long id,
        String name,
        int price,
        String info,
        String category,
        Event event,
        int discountRate,
        List<String> images
) {

    @Builder
    public ProductResponseDto {
    }

    public static ProductResponseDto of(Product product, List<String> imgUrlList) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .info(product.getInfo())
                .category(product.getCategory())
                .event(product.getEvent())
                .discountRate(product.getDiscountRate())
                .images(imgUrlList)
                .build();
    }

    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .info(product.getInfo())
                .category(product.getCategory())
                .event(product.getEvent())
                .discountRate(product.getDiscountRate())
                .build();
    }
}
