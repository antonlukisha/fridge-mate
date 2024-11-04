package com.example.fridgemate.service;

import com.example.fridgemate.entity.BudgetEntity;
import com.example.fridgemate.exception.BudgetException;
import com.example.fridgemate.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor executor;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository,
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  RedisTemplate<String, Object> redisTemplate,
                                  @Qualifier("budgetExecutor") Executor executor) {
        this.budgetRepository = budgetRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    public CompletableFuture<Optional<BudgetEntity>> addExpense(BigDecimal amount, Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<BudgetEntity> budget = budgetRepository.findByUserId(userId);

            budget.ifPresentOrElse (foundBudget -> {
                BigDecimal newSpentAmount = foundBudget.getSpentAmount().add(amount);
                BigDecimal newRemainingAmount = foundBudget.getTotalBudget().subtract(newSpentAmount);

                if (newRemainingAmount.compareTo(BigDecimal.ZERO) < 0) {
                    kafkaTemplate.send("budget-topic", "Not enough budget remaining, UID: " + userId);
                    throw new BudgetException("Not enough budget remaining.");
                }
                redisTemplate.opsForValue().set("budget-uid: " + foundBudget.getUserId(), foundBudget, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("budget-id: " + foundBudget.getId(), foundBudget, 24, TimeUnit.HOURS);
                kafkaTemplate.send("budget-topic", "Expense add is successful, UID: " + userId);
                foundBudget.setSpentAmount(newSpentAmount);
                foundBudget.setRemainingAmount(newRemainingAmount);

                budgetRepository.save(foundBudget);
            }, () -> {
                kafkaTemplate.send("budget-topic", "Budget is not found, UID: " + userId);
                throw new BudgetException("Budget is not found.");
            });

            return budget;
        }, executor);
    }

    public CompletableFuture<Optional<BudgetEntity>> getBudgetByUId(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            BudgetEntity cacheBudget = (BudgetEntity) redisTemplate.opsForValue().get("budget-uid: " + userId);
            kafkaTemplate.send("budget-topic", "Budget found in cache");
            if (cacheBudget == null) {
                Optional<BudgetEntity> gotBudget = budgetRepository.findByUserId(userId);
                gotBudget.ifPresent(budget -> {
                    redisTemplate.opsForValue().set("budget-uid: " + userId, budget, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("budget-id: " + budget.getId(), budget, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("budget-topic", "Retrieved budget by UID: " + userId + ", Amount: " + budget.getTotalBudget() + ", ID: " + budget.getId());
                });
                return gotBudget;
            }
            kafkaTemplate.send("budget-topic", "Retrieved budget by UID: " + userId + ", Amount: " + cacheBudget.getTotalBudget() + ", ID: " + cacheBudget.getId());
            return Optional.of(cacheBudget);
        }, executor);
    }

    public CompletableFuture<Optional<BigDecimal>> getRemainingAmount(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            BudgetEntity cacheBudget = (BudgetEntity) redisTemplate.opsForValue().get("budget-uid: " + userId);
            kafkaTemplate.send("budget-topic", "Budget found in cache");
            if (cacheBudget == null) {
                Optional<BudgetEntity> gotBudget = budgetRepository.findByUserId(userId);
                gotBudget.ifPresent(budget -> {
                    redisTemplate.opsForValue().set("budget-uid: " + userId, budget, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("budget-id: " + budget.getId(), budget, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("budget-topic", "Retrieved budget by UID: " + userId + ", Amount: " + budget.getTotalBudget() + ", ID: " + budget.getId());
                });
                return gotBudget.map(BudgetEntity::getRemainingAmount);
            }
            kafkaTemplate.send("budget-topic", "Retrieved budget by UID: " + userId + ", Amount: " + cacheBudget.getTotalBudget() + ", ID: " + cacheBudget.getId());
            return Optional.of(cacheBudget.getRemainingAmount());
        }, executor);
    }

    public CompletableFuture<Optional<BudgetEntity>> getBudgetById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            BudgetEntity cacheBudget = (BudgetEntity) redisTemplate.opsForValue().get("budget-id: " + id);
            kafkaTemplate.send("budget-topic", "Budget found in cache");
            if (cacheBudget == null) {
                Optional<BudgetEntity> gotBudget = budgetRepository.findById(id);
                gotBudget.ifPresent(budget -> {
                    redisTemplate.opsForValue().set("budget-uid: " + budget.getUserId(), budget, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("budget-id: " + id, budget, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("budget-topic", "Retrieved budget by ID: " + id + ", Amount: " + budget.getTotalBudget() + ", UID: " + budget.getUserId());
                });
                return gotBudget;
            }
            kafkaTemplate.send("budget-topic", "Retrieved budget by ID: " + cacheBudget.getId() + ", Amount: " + cacheBudget.getTotalBudget() + ", UID: " + cacheBudget.getUserId());
            return Optional.of(cacheBudget);
        }, executor);
    }

    public CompletableFuture<Void> deleteBudgetByUId(Long userId) {
        return CompletableFuture.runAsync(() -> {
            Optional<BudgetEntity> deletedBudget = budgetRepository.findByUserId(userId);
            budgetRepository.deleteByUserId(userId);
            deletedBudget.ifPresent(budget -> {
                redisTemplate.delete("budget-uid: " + userId);
                redisTemplate.delete("budget-id: " + budget.getId());
            });
            kafkaTemplate.send("budget-topic", "Deleted budget by UID: " + userId);
        }, executor);
    }

    public CompletableFuture<Void> deleteBudgetById(Long id) {
        return CompletableFuture.runAsync(() -> {
            Optional<BudgetEntity> deletedBudget = budgetRepository.findById(id);
            budgetRepository.deleteById(id);
            deletedBudget.ifPresent(budget -> {
                redisTemplate.delete("budget-uid: " + budget.getUserId());
                redisTemplate.delete("budget-id: " + id);
            });
            kafkaTemplate.send("budget-topic", "Deleted budget by ID: " + id);
        }, executor);
    }

    public CompletableFuture<BudgetEntity> createBudget(BudgetEntity budget) {
        return CompletableFuture.supplyAsync(() -> {
            if (isValidBudget(budget)) {
                BudgetEntity addedBudget = budgetRepository.save(budget);
                redisTemplate.opsForValue().set("budget-uid: " + addedBudget.getUserId(), addedBudget, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("budget-id: " + addedBudget.getId(), addedBudget, 24, TimeUnit.HOURS);
                kafkaTemplate.send("budget-topic", "Saved budget: " + addedBudget.getId() + ", UID: " + addedBudget.getUserId());
                return addedBudget;
            } else {
                kafkaTemplate.send("budget-topic", "Incorrect budgets data. ID: " + budget.getId() +  ", UID: " + budget.getUserId());
                throw new BudgetException("Incorrect budgets data.");
            }
        });
    }

    private boolean isValidBudget(BudgetEntity budget) {
        return budget.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0 && budget.getTotalBudget().compareTo(budget.getSpentAmount()) > 0;
    }

    public CompletableFuture<Optional<BudgetEntity>> updateLimit(Long userId, BigDecimal limit) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<BudgetEntity> budget = budgetRepository.findByUserId(userId);

            budget.ifPresentOrElse(foundBudget -> {
                if (limit.compareTo(foundBudget.getSpentAmount()) < 0) {
                    kafkaTemplate.send("budget-topic", "New limit is more than acceptable, UID: " + userId);
                    throw new BudgetException("New limit is more than acceptable.");
                }
                redisTemplate.opsForValue().set("budget-uid: " + foundBudget.getUserId(), foundBudget, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("budget-id: " + foundBudget.getId(), foundBudget, 24, TimeUnit.HOURS);
                kafkaTemplate.send("budget-topic", "Budget limit update is successful, UID: " + userId);
                foundBudget.setRemainingAmount(limit);
                budgetRepository.save(foundBudget);

            }, () -> {
                kafkaTemplate.send("budget-topic", "Budget is not found, UID: " + userId);
                throw new BudgetException("Budget is not found.");
            });
            return budget;
        }, executor);
    }
}
