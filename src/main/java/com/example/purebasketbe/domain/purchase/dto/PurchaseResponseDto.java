package com.example.purebasketbe.domain.purchase.dto;

import com.example.purebasketbe.domain.purchase.entity.Purchase;
import lombok.Builder;

import java.time.LocalDateTime;

public record PurchaseResponseDto(

        Long id,
        int totalPrice,
        LocalDateTime purchasedAt
) {
    @Builder
    public PurchaseResponseDto {
    }

    public static PurchaseResponseDto from(Purchase purchase) {
        return PurchaseResponseDto.builder()
                .totalPrice(purchase.getTotalPrice())
                .purchasedAt(purchase.getPurchasedAt())
                .build();
    }
}
