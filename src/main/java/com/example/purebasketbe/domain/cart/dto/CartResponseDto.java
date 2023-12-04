package com.example.purebasketbe.domain.cart.dto;

import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CartResponseDto {
    private final Long id;
    private final String name;
    private final int price;
    private final int amount;
    private final String category;
    private final String imageUrl;

    @Builder
    public CartResponseDto(Long id, String name, int price, int amount, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public static CartResponseDto of(Product product, Cart cart){
        return CartResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
//                .quantity(product.getQuantity())
                .category(product.getCategory())
                .imageUrl(product.getCategory())
                .build();
    }
}