package com.example.fridgemate.controller;

import com.example.fridgemate.dto.NotificationDto;
import com.example.fridgemate.exception.NotificationException;
import com.example.fridgemate.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * METHOD GET: getAllNotifications.
     * This method get all notifications.
     *
     * @param token User's type.
     * @return OK (200) or NO_CONTENT (204).
     */
    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<?>> getAllNotifications(@RequestParam("token") String token) {
        return notificationService.getAllNotifications(token)
                .thenApply(notifications -> notifications.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notifications));
    }

    /**
     * METHOD GET: getNotificationById.
     * This method send response which get notification by id.
     *
     * @param id Identity of notification.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить уведомление по идентификатору")
    @GetMapping("/id")
    public CompletableFuture<ResponseEntity<?>> getNotificationById(@RequestParam("id") Long id) {
        return notificationService.findNotificationById(id)
                .thenApply(notification -> notification.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    /**
     * METHOD POST: createNewNotification.
     * This method add user's notification.
     *
     * @param notification New notification.
     * @return OK (200).
     */
    @Operation(summary = "Добавить новое уведомление")
    @PostMapping("/new")
    public CompletableFuture<ResponseEntity<?>> createNewNotification(@Valid @RequestBody NotificationDto notification) {
        return notificationService.postNewNotification(notification)
                .thenApply(newNotification -> ResponseEntity.ok("Notification post with ID: " + newNotification.getId()));
    }

    /**
     * METHOD DELETE: deleteNotifications.
     * This method send response after deleted of notifications by user.
     *
     * @param token Token of user.
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удалить все уведомления")
    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<?>> deleteNotifications(@RequestParam("token") String token) {
        return notificationService.deleteNotifications(token)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD ExceptionHandler: handleNotificationValidationException.
     * This method is handler of MethodArgumentNotValidException.
     *
     * @param exception MethodArgumentNotValidException.
     * @return BAD_REQUEST (400).
     */
    @Operation(summary = "Не валидные входные данные")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleNotificationValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * METHOD ExceptionHandler: handleNotificationException.
     * This method is handler of NotificationException.
     *
     * @param exception NotificationException.
     * @return BAD_REQUEST (400).
     */
    @Operation(summary = "Ошибка интерфейса уведомлений")
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<String> handleNotificationException(NotificationException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
