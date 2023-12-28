package com.example.purebasketbe.global.kafka;

import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.example.purebasketbe.global.config.KafkaConfig.TOPIC_EVENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Methods for Producer
    public void sendEventToKafka(ProductResponseDto responseDto) {
        kafkaTemplate.send(TOPIC_EVENT, responseDto);
    }

    // Consumers
    // MemberService - sendEmailToMembers()
    // SseService - alarmNewEvent()

}
