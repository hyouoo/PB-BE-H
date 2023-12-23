package com.example.purebasketbe.global.kafka;

import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public static final String TOPIC_EVENT = "sale_event";

    // Methods for Producer
    public void sendEventToKafka(ProductResponseDto responseDto) {
        kafkaTemplate.send(TOPIC_EVENT, responseDto);
    }

    // Consumers
    // MemberService - sendEmailToMembers()
    // SseService - alarmNewEvent()

}
