package com.example.purebasketbe.global.tool;

import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import lombok.Builder;

public record EmailContents(
        String subject,
        String text
) {
    @Builder
    public EmailContents {
    }

    public static EmailContents from(ProductResponseDto responseDto) {
        int salePrice = responseDto.price() * (100 - responseDto.discountRate()) / 100;
        return EmailContents.builder()
                .subject("(광고) Pure Basket의 새로운 할인 이벤트가 시작됩니다!")
                .text(String.format("""
                                %s 할인 이벤트!
                                어디에도 없을 가격 %d원!!
                                한정 수량 %d개 쏩니다!!!""",
                        responseDto.name(), salePrice, responseDto.stock())
                ).build();
    }
}
