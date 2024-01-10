package com.example.purebasketbe.global.sse;

import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.purebasketbe.global.kafka.KafkaService.TOPIC_EVENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseService {

    private final SseRepository sseRepository;

    private final long DEFAULT_TIMEOUT = 60L * 60 * 1000;

    public SseEmitter subscribe(String email, String lastEventId) {
        String emitterId = email + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseRepository.save(emitterId, emitter);

        sendHeartbeat(); // 503 에러방지 더미 데이터

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            sseRepository.delete(emitterId);
        });
        return emitter;
    }

    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.MINUTES) // 3분마다 heartbeat 메세지 전달.
    public void sendHeartbeat() {
        Map<String, SseEmitter> connectionMap = sseRepository.findAllEmitters();
        if (!connectionMap.isEmpty()) {
            connectionMap.forEach((key, emitter) -> {
                try {
                    emitter.send(SseEmitter.event().id(key).name("heartbeat").data("heartbeat"));
                    log.info("하트비트 메세지 전송");
                } catch (IOException e) {
                    sseRepository.delete(key);
                    log.error("하트비트 전송 실패: {}", e.getMessage());
                }
            });
        }
    }

//    @KafkaListener(topics = TOPIC_EVENT, groupId = "${spring.kafka.consumer.group-id}")
//    private void alarmNewEvent(ProductResponseDto responseDto) {
//        Map<String, SseEmitter> connectionMap = sseRepository.findAllEmitters();
////        int salePrice = responseDto.price() * responseDto.discountRate() / 100;
//        String message = String.format("""
//                        새로운 할인 이벤트!
//                        %s %d%% 할인!! 한정수량 단 %d개!!!""",
//                responseDto.name(), responseDto.discountRate(), responseDto.stock());
//
//        if (!connectionMap.isEmpty()) {
//            connectionMap.forEach((id, emitter) -> notify(emitter, id, message));
//        }
//    }
//
//    private void notify(SseEmitter emitter, String emitterId, Object data) {
//        try {
//            emitter.send(SseEmitter.event()
//                    .id(emitterId)
//                    .data(data)); // String Type만 가능함
//        } catch (IOException e) {
//            log.error("notify 실패 : {}", e.getMessage());
//            sseRepository.delete(emitterId);
//        }
//    }


}
