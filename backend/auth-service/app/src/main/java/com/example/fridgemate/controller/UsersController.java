package com.example.fridgemate.controller;

import com.example.fridgemate.dto.UserDto;
import com.example.fridgemate.exception.UsersException;
import com.example.fridgemate.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/users")
public class UsersController {
    private final UsersService usersService;

    /**
     * METHOD GET: getAllUsers.
     * This method send response which get all users.
     *
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получение данных всех пользователей")
    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<?>> getAllUsers() {
        return usersService.getAllUsers()
                .thenApply(users -> users.isEmpty() ? ResponseEntity.ok(users) : ResponseEntity.noContent().build());
    }

    /**
     * METHOD GET: getByUserId.
     * This method send response which get user by id.
     *
     * @param id Identity of user.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получение данных пользователя по идентификатору")
    @GetMapping("/id")
    public CompletableFuture<ResponseEntity<?>> getByUserId(@Valid @RequestParam("id") Long id) {
        return usersService.findUserById(id)
                .thenApply(user -> user.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    /**
     * METHOD GET: getByUserUsername.
     * This method send response which get user by username.
     *
     * @param username Username of user.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получение данных пользователя по никнейм")
    @GetMapping("/username")
    public CompletableFuture<ResponseEntity<?>> getByUsername(@Valid @RequestParam("username") String username) {
        return usersService.findUserByUsername(username)
                .thenApply(user -> user.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    /**
     * METHOD GET: getByUserEmail.
     * This method send response which get user by email.
     *
     * @param email Email of user.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получение данных пользователя по email")
    @GetMapping("/email")
    public CompletableFuture<ResponseEntity<?>> getByEmail(@Valid @RequestParam("email") String email) {
        return usersService.findUserByEmail(email)
                .thenApply(user -> user.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    /**
     * METHOD GET: getByToken.
     * This method send response which get user by token.
     *
     * @param token Email of user.
     * @return OK (200) or NO_CONTENT (204).
     */
    @Operation(summary = "Получение данных пользователя по токен")
    @GetMapping("/token")
    public CompletableFuture<ResponseEntity<?>> getByToken(@Valid @RequestParam("token") String token) {
        return usersService.findUserByToken(token)
                .thenApply(user -> user.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.noContent().build()));
    }

    /**
     * METHOD POST: registerUser.
     * This method send response after registration of user.
     *
     * @param dto Data transfer object of user.
     * @return OK (200) or error.
     */
    @Operation(summary = "Регистрация пользователя FridgeMate")
    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<?>> registerUser(@Valid @RequestBody UserDto dto) {
        return usersService.registerUser(dto)
                .thenApply(user -> ResponseEntity.ok("User " + user.getUsername() + " successfully registered. Please confirm your email."));
    }

    /**
     * METHOD POST: loginUser.
     * This method send response after login user.
     *
     * @param name Username or email.
     * @param password Password.
     * @return OK (200) or UNAUTHORIZED (401).
     */
    @Operation(summary = "Вход в учётную запись пользователя FridgeMate")
    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<?>> loginUser(@Valid @RequestParam("name") String name,
                                                          @Valid @RequestParam("password") String password) {
        return usersService.loginUser(name, password)
                .thenApply(user -> user.map(u -> ResponseEntity.ok("Successfully login"))
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password")));
    }

    /**
     * METHOD PUT: verifiedUser.
     * This method send response after verified user.
     *
     * @param token User token.
     * @return OK (200).
     */
    @Operation(summary = "Изменение статуса на подтверждено")
    @PutMapping("/verified")
    public CompletableFuture<ResponseEntity<?>> verifiedUser(@Valid @RequestParam("token") String token) {
        return usersService.updateVerifiedStatus(token, true)
                .thenApply(result -> ResponseEntity.ok("Successfully confirm"));
    }

    /**
     * METHOD DELETE: deleteAllUser.
     * This method send response after deleted of users.
     *
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удаление всех пользователей")
    @DeleteMapping("/all")
    public CompletableFuture<ResponseEntity<?>> deleteAllUser() {
        return usersService.deleteAllUser()
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD DELETE: deleteUserById.
     * This method send response after deleted of user by id.
     *
     * @param id Identity of user.
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удаление пользователя по его идентификатору")
    @DeleteMapping("/id")
    public CompletableFuture<ResponseEntity<?>> deleteUserById(@Valid @RequestParam("id") Long id) {
        return usersService.deleteUserById(id)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD DELETE: deleteUserByToken.
     * This method send response after deleted of user by token.
     *
     * @param token Identity of token.
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удаление пользователя по его токену")
    @DeleteMapping("/token")
    public CompletableFuture<ResponseEntity<?>> deleteUserByToken(@Valid @RequestParam("token") String token) {
        return usersService.deleteUserByToken(token)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD DELETE: deleteUserByUsername.
     * This method send response after deleted of user by username.
     *
     * @param username Username of user.
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удаление пользователя по его никнейм")
    @DeleteMapping("/username")
    public CompletableFuture<ResponseEntity<?>> deleteUserByUsername(@Valid @RequestParam("username") String username) {
        return usersService.deleteUserByUsername(username)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD DELETE: deleteUserByUsername.
     * This method send response after deleted of user by email.
     *
     * @param email Email of user.
     * @return NO_CONTENT (204).
     */
    @Operation(summary = "Удаление пользователя по его email")
    @DeleteMapping("/email")
    public CompletableFuture<ResponseEntity<?>> deleteUserByEmail(@Valid @RequestParam("email") String email) {
        return usersService.deleteUserByEmail(email)
                .thenApply(result -> ResponseEntity.noContent().build());
    }

    /**
     * METHOD ExceptionHandler: handleUserValidationException.
     * This method is handler of MethodArgumentNotValidException.
     *
     * @param exception MethodArgumentNotValidException.
     * @return NO_CONTENT (204) or BAD_REQUEST (400).
     */
    @Operation(summary = "Не валидные входные данные")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleUserValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return errors;
    }

    /**
     * METHOD ExceptionHandler: handleUserRegistrationException.
     * This method is handler of UsersException.
     *
     * @param exception UsersException.
     * @return NO_CONTENT (204) or BAD_REQUEST (400).
     */
    @Operation(summary = "Ошибка пользовательского интерфейса")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsersException.class)
    public String handleUserRegistrationException(UsersException exception){
        return exception.getMessage();
    }
}
