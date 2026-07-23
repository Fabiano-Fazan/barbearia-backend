package com.barbearia.infrastructure.persistence.specifications;

import com.barbearia.domain.entities.Products;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;


public class ProductsSpecifications {

    public static Specification<Products> hasName(String name) {
        return (root, query, cb) ->{
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Products> hasCategory(String category) {
        return (root, query, cb) ->{
            if (category == null || category.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("category")), category.toLowerCase());
        };
    }

    public static Specification<Products> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("id"), id);
        };
    }

}
