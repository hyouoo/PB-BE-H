package com.example.purebasketbe.global.kafka;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import com.example.purebasketbe.domain.purchase.dto.KafkaPurchaseDto;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, ProductResponseDto> eventKafkaTemplate;
    private final KafkaTemplate<String, KafkaPurchaseDto> purchaseKafkaTemplate;

    public static final String TOPIC_EVENT = "event";
    public static final String TOPIC_PURCHASE = "purchase";

    // Methods for Producer
    public void sendEventToKafka(ProductResponseDto responseDto) {
        eventKafkaTemplate.send(TOPIC_EVENT, responseDto);
    }

    public void sendPurchaseToKafka(List<PurchaseDetail> purchaseRequestDto, Member member) {
        KafkaPurchaseDto data = KafkaPurchaseDto.of(purchaseRequestDto, member);
        purchaseKafkaTemplate.send( TOPIC_PURCHASE,  data);
    }
}
