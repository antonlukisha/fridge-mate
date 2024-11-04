package com.example.fridgemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "purchase_history")
public class PurchaseHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Field cannot be empty")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "Field cannot be empty")
    @Column(name = "product_id", nullable = false)
    private List<Long> productId;

    @NotBlank(message = "Field cannot be empty")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @NotBlank(message = "Field cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
