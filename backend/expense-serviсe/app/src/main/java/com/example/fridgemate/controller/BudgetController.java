package com.example.fridgemate.controller;

import com.example.fridgemate.entity.BudgetEntity;
import com.example.fridgemate.entity.PurchaseHistoryEntity;
import com.example.fridgemate.service.BudgetService;
import com.example.fridgemate.service.PurchaseHistoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/expense")
public class BudgetController {
    private final BudgetService budgetService;
    private final PurchaseHistoryService purchaseHistoryService;

    @Autowired
    public BudgetController(BudgetService budgetService, PurchaseHistoryService purchaseHistoryService) {
        this.budgetService = budgetService;
        this.purchaseHistoryService = purchaseHistoryService;
    }

    @GetMapping("/budget/user")
    public CompletableFuture<ResponseEntity<?>> getBudgetByUId(@RequestParam("uid") Long userId) {
        return budgetService.getBudgetByUId(userId)
                .thenApply(budget -> budget.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/budget")
    public CompletableFuture<ResponseEntity<?>> getBudgetById(@RequestParam("id") Long id) {
        return budgetService.getBudgetById(id)
                .thenApply(budget -> budget.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @PostMapping("/budget")
    public CompletableFuture<ResponseEntity<?>> createBudget(@Valid @RequestBody BudgetEntity budget) {
        return budgetService.createBudget(budget)
                .thenApply(createdBudget -> ResponseEntity.ok("Budget created for UID: " + createdBudget.getUserId()));
    }

    @DeleteMapping("/budget/user")
    public CompletableFuture<ResponseEntity<?>> deleteUserBudget(@RequestParam("uid") Long userId) {
        return budgetService.deleteBudgetByUId(userId)
                .thenApply(res -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/budget")
    public CompletableFuture<ResponseEntity<?>> deleteBudgetById(@RequestParam("id") Long id) {
        return budgetService.deleteBudgetById(id)
                .thenApply(res -> ResponseEntity.noContent().build());
    }

    @PutMapping("/budget/limit")
    public CompletableFuture<ResponseEntity<?>> updateLimitBudget(@RequestParam("uid") Long userId, @RequestParam("limit") BigDecimal limit) {
        return budgetService.updateLimit(userId, limit)
                .thenApply(addedExpense -> ResponseEntity.status(HttpStatus.CREATED).body(addedExpense));
    }

    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<?>> addExpense(@Valid @RequestBody PurchaseHistoryEntity expense) {
        return purchaseHistoryService.addToPurchaseHistory(expense)
                .thenApply(addedExpense -> ResponseEntity.ok("Expense created with ID: " + addedExpense.getId()));
    }

    @GetMapping("/remain")
    public CompletableFuture<ResponseEntity<?>> getRemainingAmount(@RequestParam("uid") Long userId) {
        return budgetService.getRemainingAmount(userId)
                .thenApply(amount -> amount.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/history")
    public CompletableFuture<ResponseEntity<?>> deleteUserHistory(@RequestParam("uid") Long userId) {
        return purchaseHistoryService.deleteHistoryByUId(userId)
                .thenApply(res -> ResponseEntity.noContent().build());
    }
}
