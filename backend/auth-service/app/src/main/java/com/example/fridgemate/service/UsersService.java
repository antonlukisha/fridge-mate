package com.example.fridgemate.service;

import com.example.fridgemate.dto.UserDto;
import com.example.fridgemate.entity.UsersEntity;
import com.example.fridgemate.exception.UsersException;
import com.example.fridgemate.repository.UsersRepository;
import com.example.fridgemate.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.Optional;

@Service
public class UsersService {
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor executor;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{3,15}$");

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        RedisTemplate<String, Object> redisTemplate,
                        @Qualifier("usersExecutor") Executor executor) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.usersRepository = usersRepository;
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    /**
     * METHOD: registerUser.
     * This method register new user.
     *
     * @param dto User's data.
     * @return {@link UsersEntity} or UsersException.
     */
    @Transactional
    public CompletableFuture<UsersEntity> registerUser(UserDto dto) {
        return CompletableFuture.supplyAsync(() -> {
            String username = dto.getUsername();
            String email = dto.getEmail();
            String password = dto.getPassword();
            if (!isUsername(username) || !isEmail(email)) {
                logger.error("Incorrect user data for registration: Username: {}, Email: {}", username, email);
                throw new UsersException("Incorrect user data.");
            }
            if (usersRepository.existsByEmail(email) || usersRepository.existsByUsername(username)) {
                logger.error("Email or username already in use: Username: {}, Email: {}", username, email);
                throw new UsersException("Email or username already use.");
            }
            UsersEntity user = new UsersEntity();
            user.setToken(JwtUtil.generateToken(username));
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setVerified(false);
            logger.info("Successfully registered user: Username: {}, Email: {}", username, email);
            redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-token: " + user.getToken(), user, 24, TimeUnit.HOURS);
            logger.info("User data cached for 24 hours after registration: Username: {}, Email: {}", user.getUsername(), user.getEmail());
            return usersRepository.save(user);
        }, executor);
    }

    /**
     * METHOD: loginUser.
     * This method login new user.
     *
     * @param name username or email.
     * @param password password.
     * @return An Optional {@link UsersEntity} or UsersException.
     */
    @Transactional
    public CompletableFuture<Optional<UsersEntity>> loginUser(String name, String password) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<UsersEntity> loginUser = isEmail(name) ? loginByEmailUser(name, password)
                    : loginByUsernameUser(name, password);
            loginUser.ifPresent(user -> {
                logger.info("User logged in: Username: {}, Email: {}", user.getUsername(), user.getEmail());
                redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("user-by-token: " + user.getToken(), user, 24, TimeUnit.HOURS);
                logger.info("User data cached for 24 hours after login: Username: {}, Email: {}", user.getUsername(), user.getEmail());
            });
            return loginUser;
        }, executor);
    }

    /**
     * METHOD: loginByUsernameUser.
     * This method login by username new user.
     *
     * @param username username.
     * @param password password.
     * @return An Optional {@link UsersEntity} or UsersException.
     */
    private Optional<UsersEntity> loginByUsernameUser(String username, String password) {
        UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-username: " + username);
        if (cachedUser == null) {
            Optional<UsersEntity> loginUser = usersRepository.findByUsername(username);
            return loginUser
                    .filter(user -> passwordEncoder.matches(password, user.getPassword()));
        }
        return Optional.of(cachedUser);
    }

    /**
     * METHOD: loginByEmailUser.
     * This method login by email new user.
     *
     * @param email email.
     * @param password password.
     * @return An Optional {@link UsersEntity} or UsersException.
     */
    private Optional<UsersEntity> loginByEmailUser(String email, String password) {
        UsersEntity cachedUser = (UsersEntity) redisTemplate.opsForValue().get("user-by-email: " + email);
        if (cachedUser == null) {
            Optional<UsersEntity> loginUser = usersRepository.findByEmail(email);
            return loginUser
                    .filter(user -> passwordEncoder.matches(password, user.getPassword()));
        }
        return Optional.of(cachedUser);
    }

    /**
     * METHOD: isEmail.
     * This method check validation of email.
     *
     * @param name Probable email.
     * @return true if email is valid else false.
     */
    private Boolean isEmail(String name) {
        return EMAIL_PATTERN.matcher(name).matches();
    }

    /**
     * METHOD: isUsername.
     * This method check validation of username.
     *
     * @param name Probable username.
     * @return true if username is valid else false.
     */
    private Boolean isUsername(String name) {
        return USERNAME_PATTERN.matcher(name).matches();
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
     * METHOD: updateVerifiedStatus.
     * This method change verified status user.
     *
     * @param token token.
     * @param verified verified status.
     * @return A message confirming that user by token change verified status.
     */
    public CompletableFuture<Void> updateVerifiedStatus(String token, boolean verified) {
        return CompletableFuture.runAsync(() -> {
            if (!isToken(token)) {
                logger.error("Incorrect token for change verified");
                throw new UsersException("Incorrect token.");
            }
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-token: " + token);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByToken(token);
                gotUser.ifPresent(user -> {
                    user.setVerified(verified);
                    usersRepository.save(user);
                    redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-token: " + token, user, 24, TimeUnit.HOURS);
                });
                logger.info("Successfully confirm");
                return;
            }
            cachedUser.setVerified(verified);
            usersRepository.save(cachedUser);
            redisTemplate.opsForValue().set("user-by-id: " + cachedUser.getUserId(), cachedUser, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-username: " + cachedUser.getUsername(), cachedUser, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-email: " + cachedUser.getEmail(), cachedUser, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-token: " + token, cachedUser, 24, TimeUnit.HOURS);
            logger.info("Successfully confirm");
        });
    }

    /**
     * METHOD: findUserByEmail.
     * This method find and get users by email from db.
     *
     * @param email Email of user.
     * @return An optional {@link UsersEntity} or UsersException.
     */
    public CompletableFuture<Optional<UsersEntity>> findUserByEmail(String email) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isEmail(email)) {
                logger.error("Incorrect email");
                throw new UsersException("Incorrect email.");
            }
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-email: " + email);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByEmail(email);
                gotUser.ifPresent(user -> {
                    redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + email, user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-token: " + user.getToken(), user, 24, TimeUnit.HOURS);
                    logger.info("User {} found", user.getUserId());
                });

                return gotUser;
            }
            logger.info("User {} found", cachedUser.getUserId());
            return Optional.of(cachedUser);
        }, executor);
    }

    /**
     * METHOD: findUserByToken.
     * This method find and get users by token from db.
     *
     * @param token Token of user.
     * @return An optional {@link UsersEntity} or UsersException.
     */
    public CompletableFuture<Optional<UsersEntity>> findUserByToken(String token) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isToken(token)) {
                logger.error("Incorrect token");
                throw new UsersException("Incorrect token.");
            }
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-token: " + token);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByToken(token);
                gotUser.ifPresent(user -> {
                    redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-token: " + token, user, 24, TimeUnit.HOURS);
                    logger.info("User {} found", user.getUserId());
                });
                return gotUser;
            }
            logger.info("User {} found", cachedUser.getUserId());
            return Optional.of(cachedUser);
        }, executor);
    }

    /**
     * METHOD: findUserByUsername.
     * This method find and get users by username from db.
     *
     * @param username Username of user.
     * @return An optional {@link UsersEntity} or UsersException.
     */
    public CompletableFuture<Optional<UsersEntity>> findUserByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isUsername(username)) {
                logger.error("Incorrect username");
                throw new UsersException("Incorrect username.");
            }
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-username: " + username);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByUsername(username);
                gotUser.ifPresent(user -> {
                    redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + username, user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-token: " + user.getToken(), user, 24, TimeUnit.HOURS);
                    logger.info("User {} found", user.getUserId());
                });
                return  gotUser;
            }
            logger.info("User {} found", cachedUser.getUserId());
            return Optional.of(cachedUser);
        }, executor);
    }

    /**
     * METHOD: findUserById.
     * This method find and get user by id from db.
     *
     * @param id Identity of user.
     * @return An optional {@link UsersEntity}.
     */
    public CompletableFuture<Optional<UsersEntity>> findUserById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-id: " + id);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findById(id);
                gotUser.ifPresent(user -> {
                    redisTemplate.opsForValue().set("user-by-id: " + id, user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-token: " + user.getToken(), user, 24, TimeUnit.HOURS);
                    logger.info("User {} found", user.getUserId());
                });
                return gotUser;
            }
            logger.info("User {} found", cachedUser.getUserId());
            return Optional.of(cachedUser);
        }, executor);
    }

    /**
     * METHOD: getAllUsers.
     * This method get all users from db.
     *
     * @return List of {@link UsersEntity}.
     */
    public CompletableFuture<List<UsersEntity>> getAllUsers() {
        return CompletableFuture.supplyAsync(() -> {
            List<UsersEntity> users = usersRepository.findAll();
            logger.info("Retrieved all users: Count: {}", users.size());
            return users;
        }, executor);
    }

    /**
     * METHOD: deleteAllUser.
     * This method delete all users from db.
     *
     * @return A message confirming that all users have been deleted or UsersException.
     */
    public CompletableFuture<Void> deleteAllUser() {
        return getAllUsers().thenAccept(users -> {
            if (users.isEmpty()) {
                logger.error("No users for delete");
                throw new UsersException("No users.");
            }
            usersRepository.deleteAll();
            logger.info("Deleted all users");
            users.forEach(user -> {
                redisTemplate.delete("user-by-id: " + user.getUserId());
                redisTemplate.delete("user-by-username: " + user.getUsername());
                redisTemplate.delete("user-by-email: " + user.getEmail());
                redisTemplate.delete("user-by-token: " + user.getToken());
            });
        }).exceptionally(exception -> {
            logger.error("Deleted users error");
            throw new UsersException("Deleted users error: " + exception.getMessage());
        });
    }

    /**
     * METHOD: deleteUserById.
     * This method delete user by id from db.
     *
     * @param id Identity of user.
     * @return A message confirming that user by id have been deleted.
     */
    public CompletableFuture<Void> deleteUserById(Long id) {
        return CompletableFuture.runAsync(() -> {
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-id: " + id);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findById(id);
                gotUser.ifPresent(user -> {
                    redisTemplate.delete("user-by-id: " + id);
                    redisTemplate.delete("user-by-username: " + user.getUsername());
                    redisTemplate.delete("user-by-email: " + user.getEmail());
                    redisTemplate.delete("user-by-token: " + user.getToken());
                });
            } else {
                redisTemplate.delete("user-by-id: " + id);
                redisTemplate.delete("user-by-username: " + cachedUser.getUsername());
                redisTemplate.delete("user-by-email: " + cachedUser.getEmail());
            }
            logger.info("Deleted user by ID: {}", id);
            usersRepository.deleteById(id);
        }, executor);
    }

    /**
     * METHOD: deleteUserByUsername.
     * This method delete users by username from db.
     *
     * @param username Username of user.
     * @return A message confirming that user by username have been deleted or UsersException.
     */
    public CompletableFuture<Void> deleteUserByUsername(String username) {
        return CompletableFuture.runAsync(() -> {
            if (!isUsername(username)) {
                logger.error("Incorrect username for delete");
                throw new UsersException("Incorrect username.");
            }
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-username: " + username);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByUsername(username);
                gotUser.ifPresent(user -> {
                    redisTemplate.delete("user-by-id: " + user.getUserId());
                    redisTemplate.delete("user-by-username: " + username);
                    redisTemplate.delete("user-by-email: " + user.getEmail());
                    redisTemplate.delete("user-by-token: " + user.getToken());
                });
            } else {
                redisTemplate.delete("user-by-id: " + cachedUser.getUserId());
                redisTemplate.delete("user-by-username: " + username);
                redisTemplate.delete("user-by-email: " + cachedUser.getEmail());
                redisTemplate.delete("user-by-token: " + cachedUser.getToken());
            }
            logger.info("Deleted user by username: {}", username);
            usersRepository.deleteByUsername(username);
        }, executor);
    }

    /**
     * METHOD: deleteUserByEmail.
     * This method delete users by email from db.
     *
     * @param email Email of user.
     * @return A message confirming that user by email have been deleted or UsersException.
     */
    public CompletableFuture<Void> deleteUserByEmail(String email) {
        return CompletableFuture.runAsync(() -> {
            if (!isEmail(email)) {
                logger.error("Incorrect email for delete");
                throw new UsersException("Incorrect email.");
            }
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-email: " + email);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByEmail(email);
                gotUser.ifPresent(user -> {
                    redisTemplate.delete("user-by-id: " + user.getUserId());
                    redisTemplate.delete("user-by-username: " + user.getUsername());
                    redisTemplate.delete("user-by-email: " + email);
                    redisTemplate.delete("user-by-token: " + user.getToken());
                });
            } else {
                redisTemplate.delete("user-by-id: " + cachedUser.getUserId());
                redisTemplate.delete("user-by-username: " + cachedUser.getUsername());
                redisTemplate.delete("user-by-email: " + email);
                redisTemplate.delete("user-by-token: " + cachedUser.getToken());
            }
            logger.info("Deleted user by email: {}", email);
            usersRepository.deleteByEmail(email);
        }, executor);
    }

    /**
     * METHOD: deleteUserByToken.
     * This method delete users by token from db.
     *
     * @param token Token of user.
     * @return A message confirming that user by token have been deleted or UsersException.
     */
    public CompletableFuture<Void> deleteUserByToken(String token) {
        return CompletableFuture.runAsync(() -> {
            if (!isToken(token)) {
                logger.error("Incorrect token for delete");
                throw new UsersException("Incorrect token.");
            }
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-token: " + token);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByToken(token);
                gotUser.ifPresent(user -> {
                    redisTemplate.delete("user-by-id: " + user.getUserId());
                    redisTemplate.delete("user-by-username: " + user.getUsername());
                    redisTemplate.delete("user-by-email: " + user.getEmail());
                    redisTemplate.delete("user-by-token: " + token);
                });
            } else {
                redisTemplate.delete("user-by-id: " + cachedUser.getUserId());
                redisTemplate.delete("user-by-username: " + cachedUser.getUsername());
                redisTemplate.delete("user-by-email: " + cachedUser.getEmail());
                redisTemplate.delete("user-by-token: " + token);
            }
            logger.info("Deleted user by token: {}", token);
            usersRepository.deleteByToken(token);
        }, executor);
    }

    /**
     * METHOD: updatePassword.
     * This method change user's password.
     *
     * @param token token.
     * @param password new password.
     * @return A message confirming that user by token change password.
     */
    public CompletableFuture<Void> updatePassword(@Valid String token, @Valid String password) {
        return CompletableFuture.runAsync(() -> {
            if (!isToken(token)) {
                logger.error("Incorrect token for change verified");
                throw new UsersException("Incorrect token.");
            }
            UsersEntity cachedUser = null;
            Object cachedUserObj = redisTemplate.opsForValue().get("user-by-token: " + token);
            if (cachedUserObj instanceof UsersEntity) {
                cachedUser = (UsersEntity) cachedUserObj;
            }
            if (cachedUser == null) {
                Optional<UsersEntity> gotUser = usersRepository.findByToken(token);
                gotUser.ifPresent(user -> {
                    user.setPassword(passwordEncoder.encode(password));
                    usersRepository.save(user);
                    redisTemplate.opsForValue().set("user-by-id: " + user.getUserId(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-username: " + user.getUsername(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-email: " + user.getEmail(), user, 24, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set("user-by-token: " + token, user, 24, TimeUnit.HOURS);
                });
                logger.info("Successfully changed password");
                return;
            }
            cachedUser.setPassword(passwordEncoder.encode(password));
            usersRepository.save(cachedUser);
            redisTemplate.opsForValue().set("user-by-id: " + cachedUser.getUserId(), cachedUser, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-username: " + cachedUser.getUsername(), cachedUser, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-email: " + cachedUser.getEmail(), cachedUser, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("user-by-token: " + token, cachedUser, 24, TimeUnit.HOURS);
            logger.info("Successfully changed password");
        });
    }
}
