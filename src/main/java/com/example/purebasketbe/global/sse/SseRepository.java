package com.example.purebasketbe.global.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseRepository {

    private final Map<String, SseEmitter> connectMap = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter emitter) {
        connectMap.put(emitterId, emitter);
        return emitter;
    }

    public void delete(String emitterId) {
        connectMap.remove(emitterId);
    }

    public Map<String, SseEmitter> findAllEmitters() {
        return connectMap;
    }
}
