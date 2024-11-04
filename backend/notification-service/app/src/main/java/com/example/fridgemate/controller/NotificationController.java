package com.example.fridgemate.controller;

import com.example.fridgemate.entity.NotificationEntity;
import com.example.fridgemate.exception.NotificationException;
import com.example.fridgemate.service.NotificationService;
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

    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<?>> getAllNotifications(@RequestParam("uid") Long userId) {
        return notificationService.getAllNotifications(userId)
                .thenApply(notifications -> notifications.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notifications));
    }

    @GetMapping("/id")
    public CompletableFuture<ResponseEntity<?>> getNotificationById(@RequestParam("id") Long id) {
        return notificationService.findNotificationById(id)
                .thenApply(notification -> notification.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @PostMapping("/new")
    public CompletableFuture<ResponseEntity<?>> createNewNotification(@Valid @RequestBody NotificationEntity newNotification) {
        return notificationService.postNewNotification(newNotification)
                .thenApply(notification -> ResponseEntity.ok("Notification created with ID: " + notification.getId()));
    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<?>> deleteNotification(@RequestParam("id") Long id) {
        return notificationService.deleteNotificationById(id)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> UserValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<String> handleUserRegistrationException(NotificationException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
