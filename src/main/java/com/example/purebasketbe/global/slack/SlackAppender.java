package com.example.purebasketbe.global.slack;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class SlackAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent eventObject) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://hooks.slack.com/services/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";
        Map<String, Object> body = createSlackErrorBody(eventObject);
        restTemplate.postForEntity(url, body, String.class);
    }

    private Map<String, Object> createSlackErrorBody(ILoggingEvent eventObject) {
        String message = createMessage(eventObject);
        return Map.of(
                "attachments", List.of(
                        Map.of(
                                "fallback", "요청을 실패했어요 :cry:",
                                "color", "#2eb886",
                                "pretext", "에러가 발생했어요 확인해주세요 :cry:",
                                "author_name", "purebasket-be",
                                "text", message,
                                "ts", eventObject.getTimeStamp()
                        )
                )
        );
    }

    private String createMessage(ILoggingEvent eventObject) {
        String baseMessage = "에러가 발생했습니다.\n";
        String pattern = baseMessage + "```%s %s %s [%s] - %s```";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format(pattern,
                simpleDateFormat.format(eventObject.getTimeStamp()),
                eventObject.getLevel(),
                eventObject.getThreadName(),
                eventObject.getLoggerName(),
                eventObject.getFormattedMessage());
    }
}
