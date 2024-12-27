package com.example.fridgemate.repository;

import com.example.fridgemate.entity.NotificationEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findAllByToken(String token);
    void deleteAllByToken(String token);
    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationEntity n WHERE n.timestamp < :lastCleanupTime")
    void deleteOldNotifications(@Param("lastCleanupTime") LocalDateTime lastCleanupTime);
}
