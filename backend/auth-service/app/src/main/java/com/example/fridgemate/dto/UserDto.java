package com.example.fridgemate.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @NotNull(message = "Field cannot be empty")
    @Size(min = 3, max = 15, message = "Username should be between 3 and 15 characters")
    private String username;

    @NotNull(message = "Field cannot be empty")
    @Size(min = 6, max = 30, message = "Password should be from 6 to 30 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password should contain at least one letter, one number, and one special character.")
    private String password;

    @NotNull(message = "Field cannot be empty")
    @Email(message = "Incorrect format email")
    private String email;
}

