package com.example.fridgemate.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ProductDto {
    @NotNull(message = "Field cannot be empty")
    @Size(max = 50, message = "Product name should be not more 50 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Field cannot be empty")
    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @NotNull(message = "Field cannot be empty")
    @FutureOrPresent(message = "Expiry date cannot be in the past")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @NotNull(message = "Field cannot be empty")
    @Min(value = 0)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotNull(message = "Field cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
