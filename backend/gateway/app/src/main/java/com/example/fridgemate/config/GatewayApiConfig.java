package com.example.fridgemate.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayApiConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product_service", path -> path.path("/api/products/**")
                        .uri("http://localhost:8081/api/products"))
                .route("recommendation-service", path -> path.path("/api/recommendations/**")
                        .uri("http://localhost:8082/api/recommendations"))
                .route("recipe_service", path -> path.path("/api/recipes/**")
                        .uri("http://localhost:8083/api/recipes"))
                .route("notification-service", path -> path.path("/api/notifications/**")
                        .uri("http://localhost:8084/api/notifications"))
                .route("auth-service", path -> path.path("/api/users/**")
                        .uri("http://localhost:8085/api/users"))
                .route("expense-service", path -> path.path("/api/expenses/**")
                        .uri("http://localhost:8086/api/expenses"))
                .build();
    }
}
