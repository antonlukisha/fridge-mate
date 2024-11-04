package com.example.fridgemate.repository;

import com.example.fridgemate.entity.PurchaseHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistoryEntity, Long> {
    List<PurchaseHistoryEntity> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
