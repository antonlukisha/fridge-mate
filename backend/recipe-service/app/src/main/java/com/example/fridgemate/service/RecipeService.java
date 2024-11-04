package com.example.fridgemate.service;

import com.example.fridgemate.entity.RecipeEntity;
import com.example.fridgemate.exception.RecipeException;
import com.example.fridgemate.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Executor executor;

    private static final Pattern INGREDIENT_PATTERN = Pattern.compile("^([\\w\\s]+([,;\\s]+[\\w\\s]+)*)$");

    @Autowired
    public RecipeService(RecipeRepository recipeRepository,
                         RedisTemplate<String, Object> redisTemplate,
                         KafkaTemplate<String, String> kafkaTemplate,
                         @Qualifier("recipesExecutor") Executor executor) {
        this.recipeRepository = recipeRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.executor = executor;
    }

    public CompletableFuture<List<RecipeEntity>> suggestRecipes() {
        return CompletableFuture.supplyAsync(() -> {
            List<RecipeEntity> availableRecipes = recipeRepository.findAll();
            kafkaTemplate.send("recipe-topic", "Retrieved suggest recipes. Count: " + availableRecipes.size());
            //получать через Gateway api рекомендации
            return availableRecipes;
        }, executor);
    }


    public CompletableFuture<List<RecipeEntity>> getAllRecipes() {
        return CompletableFuture.supplyAsync(() -> {
            List<RecipeEntity> recipes = recipeRepository.findAll();
            kafkaTemplate.send("recipe-topic", "Retrieved all recipes: Count: " + recipes.size());
            return recipes;
        }, executor);
    }

    public CompletableFuture<Optional<RecipeEntity>> findRecipeById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            RecipeEntity cachedRecipe = (RecipeEntity) redisTemplate.opsForValue().get("recipe: " + id);
            kafkaTemplate.send("recipe-topic", "Recipe found in cache");
            if (cachedRecipe == null) {
                Optional<RecipeEntity> gotRecipe = recipeRepository.findById(id);
                gotRecipe.ifPresent(recipe -> {
                    redisTemplate.opsForValue().set("recipe: " + id, recipe, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("recipe-topic", "Retrieved recipe by ID: " + id + ", Name: " + recipe.getName());
                });
                return gotRecipe;
            }
            kafkaTemplate.send("recipe-topic", "Retrieved recipe by ID: " + id + ", Name: " + cachedRecipe.getName());
            return Optional.of(cachedRecipe);
        }, executor);
    }

    private boolean isValidRecipe(RecipeEntity recipe) {
        return INGREDIENT_PATTERN.matcher(recipe.getIngredients()).matches();
    }

    public CompletableFuture<RecipeEntity> postNewRecipe(RecipeEntity recipe) {
        return CompletableFuture.supplyAsync(() -> {
            if (isValidRecipe(recipe)) {
                RecipeEntity savedRecipe = recipeRepository.save(recipe);
                redisTemplate.opsForValue().set("recipe: " + savedRecipe.getId(), savedRecipe, 24, TimeUnit.HOURS);
                kafkaTemplate.send("recipe-topic", "Saved recipe: " + savedRecipe.getId());
                return savedRecipe;
            } else {
                kafkaTemplate.send("recipe-topic", "Incorrect recipes data");
                throw new RecipeException("Incorrect recipes data.");
            }
        }, executor);
    }
}
