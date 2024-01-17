package com.example.purebasketbe.global.slack;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class SlackAppender extends AppenderBase<ILoggingEvent> {

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Override
    protected void append(final ILoggingEvent eventObject) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> body = createSlackErrorBody(eventObject);
        log.info("url : {}", webhookUrl);
        restTemplate.postForEntity(webhookUrl, body, String.class);
    }

    private Map<String, Object> createSlackErrorBody(final ILoggingEvent eventObject) {
        String message = createMessage(eventObject);
        return Map.of(
                "attachments", List.of(
                        Map.of(
                                "fallback", "에러가 발생했어요 :cry:",
                                "color", "#2eb886",
                                "pretext", "에러가 발생했어요 확인해주세요 :cry:",
                                "author_name", "purebasket-be",
                                "text", message,
                                "ts", eventObject.getTimeStamp()
                        )
                )
        );
    }

    private String createMessage(final ILoggingEvent eventObject) {
        String baseMessage = "에러가 발생했습니다.\n";
        String pattern = baseMessage + "```%s [%s] [%s] [%s] - %s```";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format(pattern,
                simpleDateFormat.format(eventObject.getTimeStamp()),
                eventObject.getLevel(),
                eventObject.getThreadName(),
                eventObject.getLoggerName(),
                eventObject.getFormattedMessage());
    }
}
