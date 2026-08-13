package com.barbearia.staff.infrastructure.persistence;

import com.barbearia.staff.domain.model.Barber;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

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

    public static Specification<Barber> hasId(UUID id){
        return (root, query, cb) ->{
            if (id == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("id"), id);
        };
    }
}
