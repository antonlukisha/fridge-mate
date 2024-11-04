package com.example.fridgemate.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic BudgetTopic() {
        return TopicBuilder.name("budget-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic PurchaseHistoryTopic() {
        return TopicBuilder.name("purchase-history-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
