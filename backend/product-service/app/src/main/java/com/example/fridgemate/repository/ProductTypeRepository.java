package com.example.fridgemate.repository;

import com.example.fridgemate.entity.ProductTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, Long> {
    Optional<ProductTypeEntity> findByName(String name);
}
