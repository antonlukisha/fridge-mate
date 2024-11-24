package com.example.fridgemate.controller;

import com.example.fridgemate.dto.ProductDto;
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
    public CompletableFuture<ResponseEntity<?>> getAllProducts(@Valid @RequestParam("token") String token) {
        return productService.getAllProducts(token)
                .thenApply(products -> products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    @GetMapping("/id")
    public  CompletableFuture<ResponseEntity<?>> getByIdProducts(@Valid @RequestParam("id") Long id) {
        return productService.findProductById(id)
                .thenApply(product -> product.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/type")
    public  CompletableFuture<ResponseEntity<?>> getByIdType(@Valid @RequestParam("type") Long type, @Valid @RequestParam("token") String token) {
        return productService.findProductByType(type, token)
                .thenApply(product -> product.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/expired")
    public  CompletableFuture<ResponseEntity<?>> getAllExpiredProducts(@Valid @RequestParam("token") String token) {
        return productService.getExpiredProducts(token)
                .thenApply(products -> products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    @GetMapping("/missing")
    public  CompletableFuture<ResponseEntity<?>> getAllMissingProducts(@Valid @RequestParam("token") String token) {
        return productService.getMissingProducts(token)
                .thenApply(products -> products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    @GetMapping("/types/all")
    public  CompletableFuture<ResponseEntity<?>> getAllProductTypes() {
        return productService.getAllProductTypes()
                .thenApply(types -> types.isEmpty() ? ResponseEntity.ok(types) : ResponseEntity.noContent().build());
    }

    @GetMapping("/types/name")
    public CompletableFuture<ResponseEntity<?>> getProductTypeByName(@Valid @RequestParam("name") String name) {
        return productService.findProductTypeByName(name)
                .thenApply(type -> type.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @PostMapping("/add")
    public  CompletableFuture<ResponseEntity<?>> addProduct(@Valid @RequestBody ProductDto product, @Valid @RequestParam("token") String token) {
        return productService.addProduct(token, product)
                .thenApply(createdProduct -> ResponseEntity.ok("Product created with ID: " + createdProduct.getId()));
    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<?>> deleteAllProduct(@Valid @RequestParam("token") String token) {
        return productService.deleteAllProducts(token)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/id")
    public CompletableFuture<ResponseEntity<?>> deleteProduct(@RequestParam("id") Long id) {
        return productService.deleteProductById(id)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/expired")
    public CompletableFuture<ResponseEntity<?>> deleteAllExpiredProduct(@Valid @RequestParam("token") String token) {
        return productService.deleteExpiredProduct(token)
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
