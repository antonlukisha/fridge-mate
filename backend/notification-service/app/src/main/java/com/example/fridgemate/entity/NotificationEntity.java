package com.example.fridgemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Поле не может быть пустым")
    @Column(name = "uid", nullable = false)
    private Long userId;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 160, message = "Длинна уведомления должна быть не более 160 символов")
    @Column(name = "message", nullable = false)
    private String message;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 3, min = 3, message = "Длинна типа данных должна быть ровно 3 символа")
    @Column(name = "type", nullable = false)
    private String notificationType;

    @NotBlank(message = "Поле не может быть пустым")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
