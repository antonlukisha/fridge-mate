package com.example.fridgemate.controller;

import com.example.fridgemate.entity.ProductEntity;
import com.example.fridgemate.exception.ProductException;
import com.example.fridgemate.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<?>> getAllProducts(@RequestParam("uid") Long userId) {
        return productService.getAllProducts(userId)
                .thenApply(products -> products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    @GetMapping("/id")
    public  CompletableFuture<ResponseEntity<?>> getByIdProducts(@RequestParam("id") Long id) {
        return productService.findProductById(id)
                .thenApply(product -> product.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/type")
    public  CompletableFuture<ResponseEntity<?>> getByIdType(@RequestParam("type") String type, @RequestParam("uid") Long userId) {
        return productService.findProductByType(type, userId)
                .thenApply(product -> product.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/expired")
    public  CompletableFuture<ResponseEntity<?>> getAllExpiredProducts(@RequestParam("uid") Long userId) {
        return productService.getExpiredProducts(userId)
                .thenApply(products -> products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    @GetMapping("/missing")
    public  CompletableFuture<ResponseEntity<?>> getAllMissingProducts(@RequestParam("uid") Long userId) {
        return productService.getMissingProducts(userId)
                .thenApply(products -> products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    @PostMapping
    public  CompletableFuture<ResponseEntity<?>> addProduct(@Valid @RequestBody ProductEntity product) {
        return productService.addProduct(product)
                .thenApply(createdProduct -> ResponseEntity.ok("Product created with ID: " + createdProduct.getId()));
    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<?>> deleteAllProduct(@RequestParam("uid") Long userId) {
        return productService.deleteAllProducts(userId)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/id")
    public CompletableFuture<ResponseEntity<?>> deleteProduct(@RequestParam("id") Long id) {
        return productService.deleteProduct(id)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/expired")
    public CompletableFuture<ResponseEntity<?>> deleteAllExpiredProduct(@RequestParam("uid") Long userId) {
        return productService.deleteExpiredProduct(userId)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> UserValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<String> handleUserRegistrationException(ProductException exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
