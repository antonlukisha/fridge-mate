package com.example.fridgemate.controller;

import com.example.fridgemate.dto.ProductDto;
import com.example.fridgemate.exception.ProductException;
import com.example.fridgemate.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
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

    /**
     * METHOD GET: getAllProducts.
     * This method get all products.
     *
     * @param token User's type.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить все продукты конкретного пользователя")
    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<?>> getAllProducts(@Valid @RequestParam("token") String token) {
        return productService.getAllProducts(token)
                .thenApply(products -> !products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    /**
     * METHOD GET: getAllProductTypesByUser.
     * This method get all product types in fridge.
     *
     * @param token User's type.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить все типы продуктов конкретного пользователя")
    @GetMapping("/all/type")
    public CompletableFuture<ResponseEntity<?>> getAllProductTypesByUser(@Valid @RequestParam("token") String token) {
        return productService.getAllProductTypesByUser(token)
                .thenApply(types -> !types.isEmpty() ? ResponseEntity.ok(types) : ResponseEntity.noContent().build());
    }

    /**
     * METHOD GET: getByIdProducts.
     * This method send response which get product by id.
     *
     * @param id Identity of product.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить продукт по идентификатору")
    @GetMapping("/id")
    public  CompletableFuture<ResponseEntity<?>> getByIdProducts(@Valid @RequestParam("id") Long id) {
        return productService.findProductById(id)
                .thenApply(product -> product.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    /**
     * METHOD GET: getByIdType.
     * This method send response which get product by type's id.
     *
     * @param type Type of product.
     * @param token User's type.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить продукт по идентификатору типа")
    @GetMapping("/type")
    public  CompletableFuture<ResponseEntity<?>> getByIdType(@Valid @RequestParam("type") Long type, @Valid @RequestParam("token") String token) {
        return productService.findProductByType(type, token)
                .thenApply(product -> product.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    /**
     * METHOD GET: getAllExpiredProducts.
     * This method send response which get expired products.
     *
     * @param token User's type.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить все просроченные продукты")
    @GetMapping("/expired")
    public  CompletableFuture<ResponseEntity<?>> getAllExpiredProducts(@Valid @RequestParam("token") String token) {
        return productService.getExpiredProducts(token)
                .thenApply(products -> !products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    /**
     * METHOD GET: getAllMissingProducts.
     * This method send response which get missing products.
     *
     * @param token User's type.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить все продукты с истекающим сроком годности")
    @GetMapping("/missing")
    public  CompletableFuture<ResponseEntity<?>> getAllMissingProducts(@Valid @RequestParam("token") String token) {
        return productService.getMissingProducts(token)
                .thenApply(products -> !products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build());
    }

    /**
     * METHOD GET: getAllProductTypes.
     * This method get all product's types.
     *
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить все типы продуктов")
    @GetMapping("/types/all")
    public  CompletableFuture<ResponseEntity<?>> getAllProductTypes() {
        return productService.getAllProductTypes()
                .thenApply(types -> !types.isEmpty() ? ResponseEntity.ok(types) : ResponseEntity.noContent().build());
    }

    /**
     * METHOD GET: getProductTypeByName.
     * This method get product type by name.
     *
     * @param name Type's name.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получить тип продукта по названию типа")
    @GetMapping("/types/name")
    public CompletableFuture<ResponseEntity<?>> getProductTypeByName(@Valid @RequestParam("name") String name) {
        return productService.findProductTypeByName(name)
                .thenApply(type -> type.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    //TODO: Create default updating product's expired date
    /**
     * METHOD POST: addProduct.
     * This method add user's product.
     *
     * @param token User token.
     * @param product New product.
     * @return OK (200).
     */
    @Operation(summary = "Добавить новый продукт в холодильник")
    @PostMapping("/add")
    public  CompletableFuture<ResponseEntity<?>> addProduct(@Valid @RequestBody ProductDto product, @Valid @RequestParam("token") String token) {
        return productService.addProduct(token, product)
                .thenApply(createdProduct -> ResponseEntity.ok("Product created with ID: " + createdProduct.getId()));
    }

    /**
     * METHOD DELETE: deleteAllProduct.
     * This method send response after deleted of products by user.
     *
     * @param token Token of user.
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удалить все продукты")
    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<?>> deleteAllProduct(@Valid @RequestParam("token") String token) {
        return productService.deleteAllProducts(token)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD DELETE: deleteProduct.
     * This method send response after deleted of product by id.
     *
     * @param id Identity of product.
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удалить продукт по идентификатору")
    @DeleteMapping("/id")
    public CompletableFuture<ResponseEntity<?>> deleteProduct(@RequestParam("id") Long id) {
        return productService.deleteProductById(id)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD DELETE: deleteAllExpiredProduct.
     * This method send response after deleted of expired products by user.
     *
     * @param token Token of user.
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удалить все просроченные продукты")
    @DeleteMapping("/expired")
    public CompletableFuture<ResponseEntity<?>> deleteAllExpiredProduct(@Valid @RequestParam("token") String token) {
        return productService.deleteExpiredProduct(token)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD ExceptionHandler: handleProductValidationException.
     * This method is handler of MethodArgumentNotValidException.
     *
     * @param exception MethodArgumentNotValidException.
     * @return BAD_REQUEST (400).
     */
    @Operation(summary = "Не валидные входные данные")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleProductValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * METHOD ExceptionHandler: handleProductException.
     * This method is handler of ProductException.
     *
     * @param exception ProductException.
     * @return BAD_REQUEST (400).
     */
    @Operation(summary = "Ошибка интерфейса холодильника")
    @ExceptionHandler(ProductException.class)
    public ResponseEntity<String> handleProductException(ProductException exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
