package com.example.fridgemate.controller;

import com.example.fridgemate.entity.RecipeEntity;
import com.example.fridgemate.exception.RecipeException;
import com.example.fridgemate.service.RecipeService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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

    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<?>> getAllRecipes() {
        return recipeService.getAllRecipes()
                .thenApply(users -> users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users));
    }

    @GetMapping("/id")
    public CompletableFuture<ResponseEntity<?>> getByIdRecipes(@RequestParam("id") Long id) {
        return recipeService.findRecipeById(id)
                .thenApply(user -> user.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/suggest")
    public CompletableFuture<ResponseEntity<?>> suggestRecipes() {
        return recipeService.suggestRecipes()
                .thenApply(users -> users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users));
    }

    @PostMapping("/new")
    public CompletableFuture<ResponseEntity<?>> createNewRecipes(@Valid @RequestBody RecipeEntity newRecipe) {
        return recipeService.postNewRecipe(newRecipe)
                .thenApply(recipe -> ResponseEntity.ok("Recipe created with ID: " + recipe.getId()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> UserValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(RecipeException.class)
    public ResponseEntity<String> handleUserRegistrationException(RecipeException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
