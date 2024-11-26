package com.example.fridgemate.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    @NotNull(message = "Field cannot be empty")
    @Size(max = 50, message = "Product name should be not more 50 characters")
    private String name;

    @NotNull(message = "Field cannot be empty")
    @Pattern(regexp = "\\d+", message = "Product type ID must be a valid number")
    private String typeId;

    @NotNull(message = "Field cannot be empty")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Expiry date should be in the format YYYY-MM-DD")
    private String expiryDate;

    @NotNull(message = "Field cannot be empty")
    @Pattern(regexp = "\\d+", message = "Quantity must be a valid number")
    private String quantity;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Added date should be in the format YYYY-MM-DD")
    private String addedDate;

    @NotNull(message = "Field cannot be empty")
    @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Amount should be a valid number with up to two decimal places")
    private String amount;
}
