package com.example.fridgemate.service;

import com.example.fridgemate.entity.RecipeEntity;
import com.example.fridgemate.repository.RecipeRepository;
import com.example.fridgemate.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor executor;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository,
                         RedisTemplate<String, Object> redisTemplate,
                         @Qualifier("recipesExecutor") Executor executor) {
        this.recipeRepository = recipeRepository;
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    /**
     * METHOD: suggestRecipes.
     * This method get all suggest recipes for user.
     *
     * @return List of {@link RecipeEntity}.
     */
    public CompletableFuture<List<RecipeEntity>> suggestRecipes(String token) {
        return CompletableFuture.supplyAsync(() -> {
            List<RecipeEntity> availableRecipes = recipeRepository.findAll();
            logger.info("Retrieved suggest recipes. Count: {}", availableRecipes.size());
            //TODO: Get throw Gateway api recommendations
            return availableRecipes;
        }, executor);
    }

    /**
     * METHOD: isToken.
     * This method check validation of token.
     *
     * @param token Probable token.
     * @return true if token is valid else false.
     */
    private Boolean isToken(String token) { return JwtUtil.validateToken(token); }

    /**
     * METHOD: getAllRecipes.
     * This method get all recipes from db.
     *
     * @return List of {@link RecipeEntity}.
     */
    public CompletableFuture<List<RecipeEntity>> getAllRecipes() {
        return CompletableFuture.supplyAsync(() -> {
            List<RecipeEntity> recipes = recipeRepository.findAll();
            logger.info("Retrieved all recipes: Count: {}", recipes.size());
            return recipes;
        }, executor);
    }

    /**
     * METHOD: findRecipeById.
     * This method find and get recipe by id from db.
     *
     * @param id Identity of recipe.
     * @return An optional {@link RecipeEntity}.
     */
    public CompletableFuture<Optional<RecipeEntity>> findRecipeById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            RecipeEntity cachedRecipe = null;
            Object cachedProductObj = redisTemplate.opsForValue().get("recipe: " + id);
            if (cachedProductObj instanceof RecipeEntity) {
                cachedRecipe = (RecipeEntity) cachedProductObj;
            }
            if (cachedRecipe == null) {
                Optional<RecipeEntity> gotRecipe = recipeRepository.findById(id);
                gotRecipe.ifPresent(recipe -> {
                    redisTemplate.opsForValue().set("recipe: " + id, recipe, 24, TimeUnit.HOURS);
                    logger.info("Recipe {} found", recipe.getId());
                });
                return gotRecipe;
            }
            logger.info("Recipe {} found", cachedRecipe.getId());
            return Optional.of(cachedRecipe);
        }, executor);
    }
}
