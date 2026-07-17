package com.barbearia.infrastructure.persistence;


import com.barbearia.domain.entities.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ProductsRepository extends JpaRepository<Products, UUID>, JpaSpecificationExecutor<Products> {
}
