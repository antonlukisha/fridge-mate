package com.example.fridgemate.controller;

import com.example.fridgemate.exception.RecipeException;
import com.example.fridgemate.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * METHOD GET: getAllRecipes.
     * This method get all recipes.
     *
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить все рецепты")
    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<?>> getAllRecipes() {
        return recipeService.getAllRecipes()
                .thenApply(recipes -> recipes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(recipes));
    }

    /**
     * METHOD GET: getByIdRecipes.
     * This method send response which get recipe by id.
     *
     * @param id Identity of recipe.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить рецепт по идентификатору")
    @GetMapping("/id")
    public CompletableFuture<ResponseEntity<?>> getByIdRecipes(@RequestParam("id") Long id) {
        return recipeService.findRecipeById(id)
                .thenApply(recipe -> recipe.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    //TODO: Integrate recommendation system for getting suggest recipes
    /**
     * METHOD GET: suggestRecipes.
     * This method get all suggest recipes.
     *
     * @param token User's token.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить все рекомендованные рецепты для конкретного пользователя")
    @GetMapping("/suggest")
    public CompletableFuture<ResponseEntity<?>> suggestRecipes(@RequestParam("token") String token) {
        return recipeService.suggestRecipes(token)
                .thenApply(recipes -> recipes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(recipes));
    }

    /**
     * METHOD ExceptionHandler: handleRecipeValidationException.
     * This method is handler of MethodArgumentNotValidException.
     *
     * @param exception MethodArgumentNotValidException.
     * @return BAD_REQUEST (400).
     */
    @Operation(summary = "Не валидные входные данные")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleRecipeValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * METHOD ExceptionHandler: handleRecipeException.
     * This method is handler of RecipeException.
     *
     * @param exception RecipeException.
     * @return BAD_REQUEST (400).
     */
    @Operation(summary = "Ошибка интерфейса рецептов")
    @ExceptionHandler(RecipeException.class)
    public ResponseEntity<String> handleRecipeException(RecipeException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
