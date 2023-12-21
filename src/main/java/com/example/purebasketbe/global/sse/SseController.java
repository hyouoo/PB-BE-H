package com.example.purebasketbe.global.sse;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.global.tool.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    @PostMapping
    public SseEmitter subscribe(@LoginAccount Member member,
                                @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {
        return sseService.subscribe(member.getEmail(), lastEventId);
    }
}
