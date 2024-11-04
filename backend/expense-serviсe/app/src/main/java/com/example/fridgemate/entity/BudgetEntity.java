package com.example.fridgemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "budget")
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Field cannot be empty")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @NotBlank(message = "Field cannot be empty")
    @DecimalMin(value = "0.0", message = "Total budget must be 0.0 or greater")
    @Column(name = "total_budget", nullable = false)
    private BigDecimal totalBudget;

    @NotBlank(message = "Field cannot be empty")
    @DecimalMin(value = "0.0", message = "Spent budget must be 0.0 or greater")
    @Column(name = "spent_amount", nullable = false)
    private BigDecimal spentAmount;

    @NotBlank(message = "Field cannot be empty")
    @DecimalMin(value = "0.0", message = "Remaining budget must be 0.0 or greater")
    @Column(name = "remaining_amount", nullable = false)
    private BigDecimal remainingAmount;

}
