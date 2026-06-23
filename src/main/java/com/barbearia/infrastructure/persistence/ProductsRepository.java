package com.barbearia.infrastructure.persistence;


import com.barbearia.domain.entities.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductsRepository extends JpaRepository<Products, UUID> {
    Page<Products> findByCategory(String category, Pageable pageable);

    Page<Products> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
