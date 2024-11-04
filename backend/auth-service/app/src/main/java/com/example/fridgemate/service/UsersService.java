package com.example.fridgemate.service;

import com.example.fridgemate.entity.UsersEntity;
import com.example.fridgemate.exception.UsersException;
import com.example.fridgemate.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Executor executor;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{3,15}$");

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        RedisTemplate<String, Object> redisTemplate,
                        KafkaTemplate<String, String> kafkaTemplate,
                        @Qualifier("usersExecutor") Executor executor) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.usersRepository = usersRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.executor = executor;
    }

    @Transactional
    public CompletableFuture<UsersEntity> registerUser(String username, String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isUsername(username) || !isEmail(email)) {
                kafkaTemplate.send("auth-topic", "Incorrect user data");
                throw new UsersException("Incorrect user data.");
            }
            if (usersRepository.existsByEmail(email) || usersRepository.existsByUsername(username)) {
                kafkaTemplate.send("auth-topic", "Email or username already use");
                throw new UsersException("Email or username already use.");
            }
            UsersEntity user = new UsersEntity();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setVerified(false);
            kafkaTemplate.send("auth-topic", "Successfully registration: Username: " + username + ", Email: " + email);
            return usersRepository.save(user);
        }, executor);
    }

    @Transactional
    public CompletableFuture<Optional<UsersEntity>> loginUser(String name, String password, Boolean remember) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<UsersEntity> loginUser = isEmail(name) ? loginByEmailUser(name, password)
                    : loginByUsernameUser(name, password);
            loginUser.ifPresent(user -> {
                kafkaTemplate.send("auth-topic", "Login user with Username: " + user.getUsername() + ", Email: " + user.getEmail());
                if (remember) {
                    redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("auth-topic", "Save user data");
                }
            });
            return loginUser;
        }, executor);
    }

    private Optional<UsersEntity> loginByUsernameUser(String username, String password) {
        UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-username: " + username);
        kafkaTemplate.send("auth-topic", "User found in cache");
        if (cachedUser == null) {
            Optional<UsersEntity> loginUser = usersRepository.findByUsername(username);
            loginUser.ifPresent(user -> {
                redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("user-by-username: " + username, user, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
            });
            return loginUser
                    .filter(user -> passwordEncoder.matches(password, user.getPassword()));
        }
        return Optional.of(cachedUser);
    }

    private Optional<UsersEntity> loginByEmailUser(String email, String password) {
        UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-email: " + email);
        kafkaTemplate.send("auth-topic", "User found in cache");
        if (cachedUser == null) {
            Optional<UsersEntity> loginUser = usersRepository.findByEmail(email);
            loginUser.ifPresent(user -> {
                redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("user-by-email: " + email, user, 24, TimeUnit.HOURS);
            });

            return loginUser
                    .filter(user -> passwordEncoder.matches(password, user.getPassword()));
        }
        return Optional.of(cachedUser);
    }

    private Boolean isEmail(String name) {
        return EMAIL_PATTERN.matcher(name).matches();
    }

    private Boolean isUsername(String name) {
        return USERNAME_PATTERN.matcher(name).matches();
    }

    public CompletableFuture<Optional<UsersEntity>> findUserByEmail(String email) {
        return CompletableFuture.supplyAsync(() -> {
            UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-email: " + email);
            kafkaTemplate.send("auth-topic", "User found in cache");
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByEmail(email);
                gotUser.ifPresent(user -> {
                    redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + email, user, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("auth-topic", "Retrieved user by Username: " + user.getUsername() + ", Email: " + email);
                });
                return gotUser;
            }
            kafkaTemplate.send("auth-topic", "Retrieved user by Username: " + cachedUser.getUsername() + ", Email: " + email);
            return Optional.of(cachedUser);
        }, executor);
    }

    public CompletableFuture<Optional<UsersEntity>> findUserByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-username: " + username);
            kafkaTemplate.send("auth-topic", "User found in cache");
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByUsername(username);
                gotUser.ifPresent(user -> {
                    redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + username, user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("auth-topic", "Retrieved user by Username: " + username + ", Email: " + user.getEmail());
                });
                return  gotUser;
            }
            kafkaTemplate.send("auth-topic", "Retrieved user by Username: " + username + ", Email: " + cachedUser.getEmail());
            return Optional.of(cachedUser);
        }, executor);
    }

    public CompletableFuture<Optional<UsersEntity>> findUserById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-id: " + id);
            kafkaTemplate.send("auth-topic", "User found in cache");
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findById(id);
                gotUser.ifPresent(user -> {
                    redisTemplate.opsForValue().set("user-by-id: " + id, user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("auth-topic", "Retrieved user by ID: " + id + ", Username: " + user.getUsername() + ", Email: " + user.getEmail());
                });
                return gotUser;
            }
            kafkaTemplate.send("auth-topic", "Retrieved user by ID: " + id + ", Username: " + cachedUser.getUsername() + ", Email: " + cachedUser.getEmail());
            return Optional.of(cachedUser);
        }, executor);
    }

    public CompletableFuture<List<UsersEntity>> getAllUsers() {
        return CompletableFuture.supplyAsync(() -> {
            List<UsersEntity> users = usersRepository.findAll();
            kafkaTemplate.send("auth-topic", "Retrieved all users: Count: " + users.size());
            return users;
        }, executor);
    }

    public CompletableFuture<Void> deleteAllUser() {
        return getAllUsers().thenAccept(users -> {
            if (users.isEmpty()) {
                kafkaTemplate.send("auth-topic", "No users");
                throw new UsersException("No users.");
            }
            usersRepository.deleteAll();
            users.forEach(user -> {
                redisTemplate.delete("user-by-id: " + user.getUserId());
                redisTemplate.delete("user-by-username: " + user.getUsername());
                redisTemplate.delete("user-by-email: " + user.getEmail());
                kafkaTemplate.send("auth-topic", "Deleted user: " + user.getUsername());
            });
            kafkaTemplate.send("auth-topic", "Deleted all users: Count: " + users.size());
        }).exceptionally(exception -> {
            kafkaTemplate.send("auth-topic", "Deleted users error: " + exception.getMessage());
            throw new UsersException("Deleted users error: " + exception.getMessage());
        });
    }

    public CompletableFuture<Void> deleteUserById(Long id) {
        return CompletableFuture.runAsync(() -> {
            UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-id: " + id);
            kafkaTemplate.send("auth-topic", "User found in cache");
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findById(id);
                gotUser.ifPresent(user -> {
                    redisTemplate.delete("user-by-id: " + id);
                    redisTemplate.delete("user-by-username: " + user.getUsername());
                    redisTemplate.delete("user-by-email: " + user.getEmail());
                });
            } else {
                redisTemplate.delete("user-by-id: " + id);
                redisTemplate.delete("user-by-username: " + cachedUser.getUsername());
                redisTemplate.delete("user-by-email: " + cachedUser.getEmail());
            }
            usersRepository.deleteById(id);
            kafkaTemplate.send("auth-topic", "Deleted user by ID: " + id);
        }, executor);
    }

    public CompletableFuture<Void> deleteUserByUsername(String username) {
        return CompletableFuture.runAsync(() -> {
            UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-username: " + username);
            kafkaTemplate.send("auth-topic", "User found in cache");
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByUsername(username);
                gotUser.ifPresent(user -> {
                    redisTemplate.delete("user-by-id: " + user.getUserId());
                    redisTemplate.delete("user-by-username: " + username);
                    redisTemplate.delete("user-by-email: " + user.getEmail());
                });
            } else {
                redisTemplate.delete("user-by-id: " + cachedUser.getUserId());
                redisTemplate.delete("user-by-username: " + username);
                redisTemplate.delete("user-by-email: " + cachedUser.getEmail());
            }
            usersRepository.deleteByUsername(username);
            kafkaTemplate.send("auth-topic", "Deleted user by username: " + username);
        }, executor);
    }

    public CompletableFuture<Void> deleteUserByEmail(String email) {
        return CompletableFuture.runAsync(() -> {
            UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-email: " + email);
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByEmail(email);
                gotUser.ifPresent(user -> {
                    redisTemplate.delete("user-by-id: " + user.getUserId());
                    redisTemplate.delete("user-by-username: " + user.getUsername());
                    redisTemplate.delete("user-by-email: " + email);
                });
            } else {
                redisTemplate.delete("user-by-id: " + cachedUser.getUserId());
                redisTemplate.delete("user-by-username: " + cachedUser.getUsername());
                redisTemplate.delete("user-by-email: " + email);
            }
            usersRepository.deleteByUsername(email);
            kafkaTemplate.send("auth-topic", "Deleted user by email: " + email);
        }, executor);
    }
}
