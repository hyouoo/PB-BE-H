package com.example.purebasketbe.domain.purchase.dto;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import lombok.Builder;

import java.util.List;

@Builder
public record KafkaPurchaseDto(List<PurchaseDetail> purchaseRequestDto, Member member) {

    public static KafkaPurchaseDto of(List<PurchaseDetail> purchaseRequestDto, Member member) {
        return KafkaPurchaseDto.builder()
                .purchaseRequestDto(purchaseRequestDto)
                .member(member)
                .build();
    }

}
