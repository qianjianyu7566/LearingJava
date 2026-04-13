package com.example.springcloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka生产者（发送消息）
 */
@Component
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // 发送消息
    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("发送成功：" + message);
    }

}
