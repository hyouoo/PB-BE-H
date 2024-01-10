package com.example.purebasketbe.domain.purchase.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

public record PurchaseRequestDto(@NotNull List<PurchaseDetail> purchaseList) {

    @Builder
    public record PurchaseDetail(@NotNull Long productId, @Min(value = 1, message = "1개 이상 입력하세요") int amount) {
    }
}