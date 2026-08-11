package com.barbearia.appointment;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class AppointmentSpecifications {

    public static Specification<Appointment> hasBarber(UUID barberId) {
        return (root, query, cb) -> {
            if (barberId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("barber").get("id"), barberId);
        };
    }

    public static Specification<Appointment> hasClient(UUID clientId) {
        return (root, query, cb) -> {
            if (clientId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("client").get("id"), clientId);
        };
    }

    public static Specification<Appointment> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("id"), id);
        };
    }

    public static Specification<Appointment> hasStatus(AppointmentStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }
}
