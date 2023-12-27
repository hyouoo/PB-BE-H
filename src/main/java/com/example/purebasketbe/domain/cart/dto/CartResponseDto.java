package com.example.purebasketbe.domain.cart.dto;

import com.example.purebasketbe.domain.cart.entity.Cart;
import lombok.Builder;

public record CartResponseDto(
        Long id,
        String name,
        Integer price,
        String category,
        String imageUrl,
        int amount
) {
    @Builder
    public CartResponseDto {

    }


    public static CartResponseDto from( Cart cart) {
        return CartResponseDto.builder()
                .id(cart.getProduct().getId())
                .name(cart.getProduct().getName())
                .price(cart.getProduct().getPrice())
                .category(cart.getProduct().getCategory())
                .imageUrl(cart.getProduct().getImages().get(0).getImgUrl())
                .amount(cart.getAmount())
                .build();
    }

}