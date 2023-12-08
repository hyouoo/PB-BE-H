package com.example.purebasketbe.domain.cart.dto;

import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.product.entity.Image;
import com.example.purebasketbe.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponseDto {

    private Long id;
    private String name;
    private Integer price;
    private String category;
    private String imageUrl;
    private int amount;

    @Builder
    private CartResponseDto(Long id, String name, Integer price, String category, String imageUrl, int amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.amount = amount;
    }

    public static CartResponseDto of(Product product, Image image, Cart cart){
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