package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponseDto {

    private Long id;
    private String name;
    private Integer price;
    private Integer quantity;
    private String info;
    private Event event;
    private String category;
    private Integer discountRate;
    private List<URL> imgUrlList;

    @Builder
    private ProductResponseDto(Long id, String name, Integer price, Integer quantity, String info,
                               Event event, String category, Integer discountRate, List<URL> imgUrlList) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.info = info;
        this.event = event;
        this.category = category;
        this.discountRate = discountRate;
        this.imgUrlList = imgUrlList;
    }

    public static ProductResponseDto of(Product product, List<URL> imageUrlList) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getStock())
                .info(product.getInfo())
                .event(product.getEvent())
                .category(product.getCategory())
                .discountRate(product.getDiscountRate())
                .imgUrlList(imageUrlList)
                .build();
    }
}
