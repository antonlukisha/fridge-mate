package com.example.fridgemate.service;

import com.example.fridgemate.entity.PurchaseHistoryEntity;
import com.example.fridgemate.exception.PurchaseHistoryException;
import com.example.fridgemate.repository.PurchaseHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class PurchaseHistoryService {
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final BudgetService budgetService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor executor;

    @Autowired
    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository,
                                  BudgetService budgetService,
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  RedisTemplate<String, Object> redisTemplate,
                                  @Qualifier("purchaseHistoryExecutor") Executor executor) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.budgetService = budgetService;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    public CompletableFuture<PurchaseHistoryEntity> addToPurchaseHistory(PurchaseHistoryEntity expense) {
        return budgetService.addExpense(expense.getAmount(), expense.getUserId()).thenApply(result -> {
            if (isValidExpense(expense)) {
                PurchaseHistoryEntity savedExpense = purchaseHistoryRepository.save(expense);
                redisTemplate.opsForValue().set("purchase: " + savedExpense.getUserId(), savedExpense, 24, TimeUnit.HOURS);
                kafkaTemplate.send("purchase-history-topic", "Saved expense: " + savedExpense.getId());
                return savedExpense;
            } else {
                kafkaTemplate.send("purchase-history-topic", "Incorrect expense data");
                throw new PurchaseHistoryException("Incorrect expense data.");
            }
        }).exceptionally(exception -> {
            kafkaTemplate.send("purchase-history-topic", "Added expense error: " + exception.getMessage() + ", UID: " + expense.getId());
            throw new PurchaseHistoryException("Added expense error: " + exception.getMessage());
        });
    }

    private boolean isValidExpense(PurchaseHistoryEntity expense) {
        return expense.getAmount().compareTo(BigDecimal.ZERO) > 0
                && !expense.getProductId().isEmpty();
    }

    public CompletableFuture<Void> deleteHistoryByUId(Long userId) {
        return CompletableFuture.runAsync(() -> {
            List<PurchaseHistoryEntity> deletedHistory = purchaseHistoryRepository.findAllByUserId(userId);
            if (!deletedHistory.isEmpty()) {
                deletedHistory.forEach(history -> redisTemplate.delete("purchase: " + userId));
                kafkaTemplate.send("purchase-history-topic", "Deleted history by UID: " + userId);
            }
            purchaseHistoryRepository.deleteAllByUserId(userId);
        }, executor);
    }
}
