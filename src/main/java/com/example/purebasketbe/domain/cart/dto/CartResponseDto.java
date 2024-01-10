package com.example.purebasketbe.domain.cart.dto;

import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import lombok.Builder;

public record CartResponseDto(
        Long id,
        String name,
        Integer price,
        String category,
        String imageUrl,
        int amount,

        Event event,

        int discountRate

) {
    @Builder
    public CartResponseDto {

    }


    public static CartResponseDto from( Cart cart) {
        Product product = cart.getProduct();
        return CartResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(product.getImages().get(0).getImgUrl())
                .event(product.getEvent())
                .discountRate(product.getDiscountRate())
                .amount(cart.getAmount())
                .build();
    }

}