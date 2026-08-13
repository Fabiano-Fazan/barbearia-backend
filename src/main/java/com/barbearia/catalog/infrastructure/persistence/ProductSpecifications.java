package com.barbearia.catalog.infrastructure.persistence;

import com.barbearia.catalog.domain.model.Product;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;


public class ProductSpecifications {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) ->{
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) ->{
            if (category == null || category.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("category")), "%" + category.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("id"), id);
        };
    }

}
