package com.example.fridgemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Field cannot be empty")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "Field cannot be empty")
    @Size(max = 30, message = "Product type should be not more 30 characters")
    @Column(name = "type", nullable = false)
    private String type;

    @NotBlank(message = "Field cannot be empty")
    @FutureOrPresent(message = "Expiry date cannot be in the past")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @NotBlank(message = "Field cannot be empty")
    @Min(value = 0)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotBlank(message = "Field cannot be empty")
    @PastOrPresent(message = "Added date cannot be in the future")
    @Column(name = "added_date", nullable = false)
    private LocalDate addedDate;

    @NotBlank(message = "Field cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
