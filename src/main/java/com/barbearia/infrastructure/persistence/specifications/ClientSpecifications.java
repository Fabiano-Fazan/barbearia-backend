package com.barbearia.infrastructure.persistence.specifications;

import com.barbearia.domain.entities.Client;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ClientSpecifications {

    public static Specification<Client> hasName(String name) {
        return (root, query, cb) ->{
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Client> hasPhone(String phone) {
        return (root, query, cb) ->{
            if (phone == null || phone.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("phone")), phone.toLowerCase());
        };
    }

    public static Specification<Client> hasId(UUID id) {
        return (root, query, cb) ->{
            if (id == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("id"), id);
        };
    }
}
