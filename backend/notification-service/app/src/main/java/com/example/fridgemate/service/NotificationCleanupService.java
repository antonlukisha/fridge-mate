package com.example.fridgemate.service;

import com.example.fridgemate.entity.CleanupData;
import com.example.fridgemate.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class NotificationCleanupService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationCleanupService.class);
    private static final String LAST_CLEANUP_FILE = "src/main/resources/static/last_cleanup_time.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private NotificationRepository notificationRepository; // Репозиторий для работы с уведомлениями

    @Transactional
    public void clearNotifications(LocalDateTime lastCleanupTime) {
        notificationRepository.deleteOldNotifications(lastCleanupTime);
        logger.info("Save new last clean up time");
        updateLastCleanupTime();
    }

    @Transactional
    private void updateLastCleanupTime() {
        try {
            CleanupData data = new CleanupData();
            data.setLastCleanupTime(LocalDateTime.now());
            objectMapper.writeValue(new File(LAST_CLEANUP_FILE), data);  // Запись в файл
        } catch (IOException exception) {
            logger.error("Updating clean up time is fail");
        }
    }

    private LocalDateTime readLastCleanupTime() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LAST_CLEANUP_FILE))) {
            CleanupData data = objectMapper.readValue(new File(LAST_CLEANUP_FILE), CleanupData.class);
            return data.getLastCleanupTime();
        } catch (IOException exception) {
            logger.error("Reading clean up time is fail");
            return LocalDateTime.MIN;
        }
    }

    @Scheduled(fixedRate = 86400000)
    public void scheduledCleanup() {
        logger.info("Read from file last clean up time");
        LocalDateTime lastCleanupTime = readLastCleanupTime();
        if (Duration.between(lastCleanupTime, LocalDateTime.now()).toHours() >= 24) {
            logger.info("Cleaning up old notifications...");
            clearNotifications(lastCleanupTime);
        }
    }
}
