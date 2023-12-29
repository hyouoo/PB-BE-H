package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.purchase.PurchaseService;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import com.example.purebasketbe.global.kafka.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseFacade {

    private final PurchaseService purchaseService;
    private final KafkaService kafkaService;

    public void purchaseProducts(List<PurchaseDetail> purchaseRequestDto, Member member) {
        int size = purchaseRequestDto.size();

        List<Long> requestedProductsIds = purchaseRequestDto.stream()
                .map(PurchaseDetail::productId).toList();
        purchaseService.processPurchase(purchaseRequestDto, requestedProductsIds, size);

        log.info("회원 {}: 상품 구매 요청 적재", member.getId());
        kafkaService.sendPurchaseToKafka(purchaseRequestDto, member);
    }

}
