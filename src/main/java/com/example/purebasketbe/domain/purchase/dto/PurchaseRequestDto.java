package com.example.purebasketbe.domain.purchase.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseRequestDto {

    private List<PurchaseDetail> purchaseList;

    private static class PurchaseDetail {
        private Long productId;
        private int amount;
    }
}

