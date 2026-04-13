package com.example.springcloud.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka消费者（监听消息）
 */
@Component
public class KafkaConsumerService {

    // 监听 test-topic 主题
    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(String message) {
        System.out.println("收到消息 ==> " + message);
    }

}
