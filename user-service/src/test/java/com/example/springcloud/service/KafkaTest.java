package com.example.springcloud.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaTest {

    @Autowired
    private KafkaProducerService producer;

    @Test
    public void testSend() {
        producer.send("test-topic", "你好 Kafka！");
    }
}