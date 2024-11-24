package com.example.fridgemate.repository;

import com.example.fridgemate.entity.ProductEntity;
import com.example.fridgemate.entity.ProductTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<List<ProductEntity>> findAllByTypeAndToken(ProductTypeEntity type, String token);
    List<ProductEntity> findAllByToken(String token);
}
