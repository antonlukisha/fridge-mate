package com.example.fridgemate.controller;

import com.example.fridgemate.exception.UsersException;
import com.example.fridgemate.service.UsersService;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/users")
public class UsersController {
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<?>> getAllUsers() {
        return usersService.getAllUsers()
                .thenApply(users -> users.isEmpty() ? ResponseEntity.ok(users) : ResponseEntity.noContent().build());
    }

    @GetMapping("/id")
    public CompletableFuture<ResponseEntity<?>> getByUserId(@RequestParam("id") Long id) {
        return usersService.findUserById(id)
                .thenApply(user -> user.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/username")
    public CompletableFuture<ResponseEntity<?>> getByUsername(@RequestParam("username") String username) {
        return usersService.findUserByUsername(username)
                .thenApply(user -> user.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/email")
    public CompletableFuture<ResponseEntity<?>> getByEmail(@RequestParam("email") String email) {
        return usersService.findUserByEmail(email)
                .thenApply(user -> user.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<?>> registerUser(@NotBlank(message = "Field cannot be empty") @RequestParam("username") String username,
                                                             @NotBlank(message = "Field cannot be empty") @RequestParam("email") String email,
                                                             @NotBlank(message = "Field cannot be empty") @RequestParam("password") String password) {
        return usersService.registerUser(username, email, password)
                .thenApply(user -> ResponseEntity.ok("User " + user.getUsername() + " successfully registered. Please confirm your email."));
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<?>> loginUser(@NotBlank(message = "Field cannot be empty") @RequestParam("name") String name,
                                                          @NotBlank(message = "Field cannot be empty") @RequestParam("password") String password,
                                                          @NotBlank(message = "Field cannot be empty") @RequestParam("remember") Boolean remember) {
        return usersService.loginUser(name, password, remember)
                .thenApply(user -> user.map(u -> ResponseEntity.ok("Successfully login"))
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password")));
    }

    @DeleteMapping("/all")
    public CompletableFuture<ResponseEntity<?>> deleteAllUser() {
        return usersService.deleteAllUser()
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/id")
    public CompletableFuture<ResponseEntity<?>> deleteUserById(@RequestParam("id") Long id) {
        return usersService.deleteUserById(id)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/username")
    public CompletableFuture<ResponseEntity<?>> deleteUserByUsername(@RequestParam("username") String username) {
        return usersService.deleteUserByUsername(username)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/email")
    public CompletableFuture<ResponseEntity<?>> deleteUserByEmail(@RequestParam("email") String email) {
        return usersService.deleteUserByEmail(email)
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

    @ExceptionHandler(UsersException.class)
    public ResponseEntity<String> handleUserRegistrationException(UsersException exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
