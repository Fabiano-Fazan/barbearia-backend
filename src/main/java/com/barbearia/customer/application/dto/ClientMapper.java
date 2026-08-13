package com.barbearia.customer.application.dto;
import com.barbearia.customer.domain.model.Client;
import org.springframework.stereotype.Component;
@Component public class ClientMapper {
    public ClientResponseDTO toResponse(Client c) {
        return new ClientResponseDTO(
                c.getId(),
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                c.getAddress());
    }
}
