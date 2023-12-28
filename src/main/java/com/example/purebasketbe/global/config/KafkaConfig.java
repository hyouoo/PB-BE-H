package com.example.purebasketbe.global.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfig {

    public static final String TOPIC_EVENT = "event";

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(TOPIC_EVENT)
                .partitions(2)
                .replicas(2)
                .build();
    }

}
