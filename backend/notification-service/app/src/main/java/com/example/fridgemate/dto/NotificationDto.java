package com.example.fridgemate.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDto {

    @NotNull(message = "Field cannot be empty")
    @Column(name = "token", nullable = false)
    private String token;

    @NotNull(message = "Field cannot be empty")
    @Column(name = "message", nullable = false)
    private String message;

    @NotNull(message = "Field cannot be empty")
    @Column(name = "type", nullable = false)
    @Pattern(regexp = "[A-Z]{3}", message = "Notification type should be in the format")
    private String notificationType;

    @Column(name = "timestamp", nullable = false)
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", message = "Notification date should be in the format YYYY-MM-DDTHH:MM:SS")
    private String timestamp;
}
