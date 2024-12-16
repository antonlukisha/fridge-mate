package com.example.fridgemate.service;

import com.example.fridgemate.dto.NotificationDto;
import com.example.fridgemate.entity.NotificationEntity;
import com.example.fridgemate.exception.NotificationException;
import com.example.fridgemate.repository.NotificationRepository;
import com.example.fridgemate.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor executor;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               RedisTemplate<String, Object> redisTemplate,
                               @Qualifier("notificationExecutor") Executor executor) {
        this.notificationRepository = notificationRepository;
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    public CompletableFuture<List<NotificationEntity>> getAllNotifications(String token) {
        return CompletableFuture.supplyAsync(() -> {
            List<NotificationEntity> notifications = notificationRepository.findAllByToken(token);
            logger.info("Retrieved all notifications: Count: {}, Token: {}", notifications.size(), token);
            return notifications;
        }, executor);
    }

    public CompletableFuture<Optional<NotificationEntity>> findNotificationById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            NotificationEntity cachedNotification = null;
            Object cachedNotificationObj = redisTemplate.opsForValue().get("notification: " + id);
            if (cachedNotificationObj instanceof NotificationEntity) {
                cachedNotification = (NotificationEntity) cachedNotificationObj;
            }
            if (cachedNotification == null) {
                Optional<NotificationEntity> foundNotification = notificationRepository.findById(id);
                foundNotification.ifPresent(notification -> {
                    redisTemplate.opsForValue().set("notification: " + id, notification, 24, TimeUnit.HOURS);
                    logger.info("Notification {} found", notification.getId());
                });
                return foundNotification;
            }
            logger.info("Notification {} found", cachedNotification.getId());
            return Optional.of(cachedNotification);
        }, executor);
    }

    /**
     * METHOD: isToken.
     * This method check validation of token.
     *
     * @param token Probable token.
     * @return true if token is valid else false.
     */
    private Boolean isToken(String token) { return JwtUtil.validateToken(token); }

    @Transactional
    public CompletableFuture<NotificationEntity> postNewNotification(NotificationDto dto) {
        return CompletableFuture.supplyAsync(() -> {
            LocalDateTime timestamp;
            if (dto.getTimestamp() == null) {
                timestamp = LocalDateTime.now();
            } else {
                timestamp = LocalDateTime.parse(dto.getTimestamp());
            }
            if (!isValidType(dto.getNotificationType()) || !isToken(dto.getToken())) {
                logger.error("Incorrect notification data");
                throw new NotificationException("Incorrect notification data.");
            }
            NotificationEntity notification = new NotificationEntity();
            notification.setToken(dto.getToken());
            notification.setNotificationType(dto.getNotificationType());
            notification.setMessage(dto.getMessage());
            notification.setTimestamp(timestamp);
            NotificationEntity savedNotification = notificationRepository.save(notification);
            redisTemplate.opsForValue().set("notification: " + savedNotification.getId(), savedNotification, 24, TimeUnit.HOURS);
            logger.info("Notification added with id: {}", savedNotification.getId());
            return savedNotification;
        }, executor);
    }

    private boolean isValidType(String type) {
        List<String> validNotificationTypes = List.of("INF", "ERR", "WAR");
        return validNotificationTypes.contains(type);
    }

    public CompletableFuture<Void> deleteNotifications(String token) {
        return getAllNotifications(token).thenAccept(notifications -> {
            if (notifications.isEmpty()) {
                logger.error("No notifications for delete");
                throw new NotificationException("No notifications.");
            }
            notificationRepository.deleteAll();
            logger.info("Deleted all notifications");
            notifications.forEach(notification -> {
                redisTemplate.delete("notification: " + notification.getId());
            });
        }).exceptionally(exception -> {
            logger.error("Deleted notifications error");
            throw new NotificationException("Deleted notifications error: " + exception.getMessage());
        });
    }
}
