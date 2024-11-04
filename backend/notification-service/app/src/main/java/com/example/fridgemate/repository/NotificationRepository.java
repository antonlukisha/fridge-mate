package com.example.fridgemate.repository;

import com.example.fridgemate.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
