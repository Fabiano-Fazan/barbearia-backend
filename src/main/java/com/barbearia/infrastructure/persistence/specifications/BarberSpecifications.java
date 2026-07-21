package com.barbearia.infrastructure.persistence.specifications;

import com.barbearia.domain.entities.Barber;
import org.springframework.data.jpa.domain.Specification;

public class BarberSpecifications {

    public static Specification<Barber> hasName(String name){
        return (root, query, cb) ->{
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Barber> hasPhone(String phone){
        return (root,query,cb) ->{
            if (phone == null || phone.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
        };
    }
}
