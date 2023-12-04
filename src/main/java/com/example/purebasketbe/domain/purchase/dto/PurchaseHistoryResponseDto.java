package com.example.purebasketbe.domain.purchase.dto;

import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PurchaseHistoryResponseDto {
    private final Long productId;
    private final String  name;
    private final int amount;
    private final int price;
    private final int totalPrice;
    private final LocalDateTime purchasedAt;

    @Builder
    private PurchaseHistoryResponseDto(Long productId, String name, int amount, int price, int totalPrice, LocalDateTime purchasedAt) {
        this.productId = productId;
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.totalPrice = totalPrice;
        this.purchasedAt = purchasedAt;
    }

    public static PurchaseHistoryResponseDto of(Product product, Purchase purchase) {
        return PurchaseHistoryResponseDto.builder()
            .productId(product.getId())
            .name(product.getName())
            .amount(purchase.getAmount())
            .price(purchase.getPrice())
            .totalPrice(purchase.getPrice() * purchase.getAmount())
            .purchasedAt(purchase.getPurchasedAt())
            .build();
    }
}

