package com.example.fridgemate.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @KafkaListener(topics = "product-topic", groupId = "product-service-id")
    public void listen(String message) {
        System.out.println("Message from Product service: " + message);
    }
}
