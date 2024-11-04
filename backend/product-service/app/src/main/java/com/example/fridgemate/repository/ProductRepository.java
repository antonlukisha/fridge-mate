package com.example.fridgemate.repository;

import com.example.fridgemate.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<List<ProductEntity>> findAllByTypeAndUserId(String type, Long userId);
    List<ProductEntity> findAllByUserId(Long userId);
}
