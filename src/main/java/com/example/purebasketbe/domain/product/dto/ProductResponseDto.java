package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponseDto {

    private Long id;
    private String name;
    private int price;
    private int stock;
    private String info;
    private String category;
    private Event event;
    private int discountRate;
    private List<String> images;

    @Builder
    ProductResponseDto(Long id, String name, int price, int stock, String info,
                       String category, Event event, int discountRate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.info = info;
        this.category = category;
        this.event = event;
        this.discountRate = discountRate;
    }

    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .info(product.getInfo())
                .category(product.getCategory())
                .event(product.getEvent())
                .discountRate(product.getDiscountRate())
                .build();
    }

    public static ProductResponseDto of(Product product, List<String> imgUrlLsit) {
        ProductResponseDto productResponseDto = ProductResponseDto.from(product);
        productResponseDto.addImgUrls(imgUrlLsit);
        return productResponseDto;
    }

    private void addImgUrls(List<String> imgUrlList) {
        this.images = imgUrlList;
    }
}
