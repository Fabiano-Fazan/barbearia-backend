package com.barbearia.staff.application.dto;
import com.barbearia.staff.domain.model.Barber;
import org.springframework.stereotype.Component;
@Component public class BarberMapper {

    public BarberResponseDTO toResponse(Barber b) {
        return toResponse(b, null);
    }
    public BarberResponseDTO toResponse(Barber b, String temporaryPassword) {
        return new BarberResponseDTO(
                b.getId(),
                b.getName(),
                b.getPhone(),
                b.getEmail(),
                b.getSpecialtyIds(),
                b.getCommissionRate(),
                temporaryPassword);
    }
}
