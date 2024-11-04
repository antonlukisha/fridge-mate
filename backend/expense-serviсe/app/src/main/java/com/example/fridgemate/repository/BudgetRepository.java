package com.example.fridgemate.repository;

import com.example.fridgemate.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {
    Optional<BudgetEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
