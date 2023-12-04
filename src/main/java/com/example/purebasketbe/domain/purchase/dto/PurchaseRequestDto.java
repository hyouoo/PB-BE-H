package com.example.purebasketbe.domain.purchase.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseRequestDto {

    @NotNull
    private List<PurchaseDetail> purchaseList;

    @Getter
    @Builder
    public static class PurchaseDetail {

        @NotNull
        private Long productId;

        @Min(value = 1, message = "1개 이상 입력하세요")
        private int amount;
    }

}

