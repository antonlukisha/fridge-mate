package com.example.fridgemate.controller;

import com.example.fridgemate.exception.RecommendationException;
import com.example.fridgemate.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
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
        return recommendationService.suggestRecipes(token)
                .thenApply(recommendations -> recommendations.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(recommendations));
    }

    /**
     * METHOD ExceptionHandler: handleRecommendationValidationException.
     * This method is handler of MethodArgumentNotValidException.
     *
     * @param exception MethodArgumentNotValidException.
     * @return BAD_REQUEST (400).
     */
    @Operation(summary = "Не валидные входные данные")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleRecommendationValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * METHOD ExceptionHandler: handleRecommendationException.
     * This method is handler of RecipeException.
     *
     * @param exception RecipeException.
     * @return BAD_REQUEST (400).
     */
    @Operation(summary = "Ошибка интерфейса рекомендаций")
    @ExceptionHandler(RecommendationException.class)
    public ResponseEntity<String> handleRecommendationException(RecommendationException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
