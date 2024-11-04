package com.example.fridgemate.service;

import com.example.fridgemate.entity.NotificationEntity;
import com.example.fridgemate.exception.NotificationException;
import com.example.fridgemate.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Executor executor;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               RedisTemplate<String, Object> redisTemplate,
                               KafkaTemplate<String, String> kafkaTemplate,
                               @Qualifier("notificationExecutor") Executor executor) {
        this.notificationRepository = notificationRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.executor = executor;
    }

    public CompletableFuture<List<NotificationEntity>> getAllNotifications(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<NotificationEntity> notifications = notificationRepository.findAllByUserId(userId);
            kafkaTemplate.send("notification-topic", "Retrieved all notifications: Count: " + notifications.size() + ", UID: " + userId);
            return notifications;
        }, executor);
    }

    public CompletableFuture<Optional<NotificationEntity>> findNotificationById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            NotificationEntity cachedNotification = (NotificationEntity) redisTemplate.opsForValue().get("notification: " + id);
            if (cachedNotification == null) {
                Optional<NotificationEntity> foundNotification = notificationRepository.findById(id);
                foundNotification.ifPresent(notification -> {
                    redisTemplate.opsForValue().set("notification: " + id, notification, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("notification-topic", "Retrieved notification by ID: " + id);
                });
                return foundNotification;
            }
            kafkaTemplate.send("notification-topic", "Retrieved notification by ID from cache: " + id);
            return Optional.of(cachedNotification);
        }, executor);
    }

    public CompletableFuture<NotificationEntity> postNewNotification(NotificationEntity notification) {
        return CompletableFuture.supplyAsync(() -> {
            if (isValidNotification(notification)) {
                NotificationEntity savedNotification = notificationRepository.save(notification);
                redisTemplate.opsForValue().set("notification: " + savedNotification.getId(), savedNotification, 24, TimeUnit.HOURS);
                kafkaTemplate.send("notification-topic", "Saved notification: " + savedNotification.getId());
                return savedNotification;
            } else {
                kafkaTemplate.send("notification-topic", "Incorrect notification data. ID: " + notification.getId() + ", UID: " + notification.getUserId());
                throw new NotificationException("Incorrect notification data.");
            }
        }, executor);
    }

    private boolean isValidNotification(NotificationEntity notification) {
        List<String> validNotificationTypes = List.of("LIM", "EXP", "WAR");
        return validNotificationTypes.contains(notification.getNotificationType());
    }

    public CompletableFuture<Void> deleteNotificationById(Long id) {
        return CompletableFuture.runAsync(() -> {
            notificationRepository.deleteById(id);
            redisTemplate.delete("notification: " + id);
            kafkaTemplate.send("notification-topic", "Deleted notification with ID: " + id);
        }, executor);
    }
}
