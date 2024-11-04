package com.example.fridgemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "recipes")
public class RecipeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Название рецепта должно быть не более 30 символов")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Поле не может быть пустым")
    @Column(name = "ingredients", nullable = false, length = 1000)
    private String ingredients;

    @Column(name = "serving", nullable = false)
    private int serving;

    @NotBlank(message = "Поле не может быть пустым")
    @Column(name = "instructions", nullable = false, length = 100000)
    private String instructions;
}
