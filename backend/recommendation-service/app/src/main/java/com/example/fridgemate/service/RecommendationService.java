package com.example.fridgemate.service;

import com.example.fridgemate.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor executor;

    @Autowired
    public RecommendationService(RedisTemplate<String, Object> redisTemplate,
                                 @Qualifier("recipesExecutor") Executor executor) {
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    /**
     * METHOD: suggestRecipes.
     * This method get all suggest recipes for user.
     *
     * @return Something.
     */
    public CompletableFuture<List<Integer>> suggestRecipes(String token) {
        return CompletableFuture.supplyAsync(() -> {
            //TODO: Get throw Gateway api recommendations
            return List.of(0);
        }, executor);
    }


    /**
     * METHOD: isToken.
     * This method check validation of token.
     *
     * @param token Probable token.
     * @return true if token is valid else false.
     */
    private Boolean isToken(String token) { return JwtUtil.validateToken(token);}
}
