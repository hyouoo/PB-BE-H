package com.example.purebasketbe.domain.cart.dto;

import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.product.entity.Image;
import com.example.purebasketbe.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

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

    public static CartResponseDto of(Product product, Image image, Cart cart) {
        return CartResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(image.getImgUrl())
                .amount(cart.getAmount())
                .build();
    }
}