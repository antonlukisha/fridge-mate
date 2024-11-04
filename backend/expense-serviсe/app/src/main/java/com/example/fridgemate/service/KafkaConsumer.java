package com.example.fridgemate.service;

import org.springframework.kafka.annotation.KafkaListener;

public class KafkaConsumer {
    @KafkaListener(topics = "budget-topic", groupId = "expense-service-id")
    public void listenBudgetTopic(String message) {
        System.out.println("Message from Expense service(budget-topic): " + message);
    }

    @KafkaListener(topics = "purchase-history-topic", groupId = "expense-service-id")
    public void listenPurchaseHistoryTopic(String message) {
        System.out.println("Message from Expense service(purchase-history-topic): " + message);
    }
}
