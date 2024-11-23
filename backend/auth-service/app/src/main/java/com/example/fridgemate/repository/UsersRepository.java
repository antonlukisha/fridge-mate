package com.example.fridgemate.repository;

import com.example.fridgemate.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByUsername(String username);
    Optional<UsersEntity> findByEmail(String email);
    Optional<UsersEntity> findByToken(String token);

    void deleteByUsername(String username);
    void deleteByEmail(String email);
    void deleteByToken(String token);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
