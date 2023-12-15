package com.example.purebasketbe.domain.purchase.dto;

import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public record PurchaseResponseDto(

        Long productId,
        String name,
        int amount,
        int price,
        int totalPrice,
        LocalDateTime purchasedAt
) {
    @Builder
    public PurchaseResponseDto {
    }

    public static PurchaseResponseDto of(Product product, Purchase purchase) {
        return PurchaseResponseDto.builder()
                .productId(product.getId())
                .name(product.getName())
                .amount(purchase.getAmount())
                .price(purchase.getPrice())
                .totalPrice(purchase.getPrice() * purchase.getAmount())
                .purchasedAt(purchase.getPurchasedAt())
                .build();
    }
}
