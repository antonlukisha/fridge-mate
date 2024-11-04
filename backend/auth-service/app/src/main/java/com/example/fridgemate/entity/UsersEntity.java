package com.example.fridgemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Field cannot be empty")
    @Size(min = 3, max = 15, message = "Username should be from 3 to 15 characters")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Field cannot be empty")
    @Email(message = "Incorrect format email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Field cannot be empty")
    @Size(min = 6, max = 30, message = "Password should be from 6 to 30 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "verified", nullable = false)
    private Boolean verified;
}
