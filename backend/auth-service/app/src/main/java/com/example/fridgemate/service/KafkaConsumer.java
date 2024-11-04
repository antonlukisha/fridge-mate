package com.example.fridgemate.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @KafkaListener(topics = "auth-topic", groupId = "auth-service-id")
    public void listen(String message) {
        System.out.println("Message from Users service: " + message);
    }
}
