package com.barbearia.application.service;

import com.barbearia.application.dto.request.BarberResquestDTO;
import com.barbearia.application.dto.response.BarberResponseDTO;
import com.barbearia.application.util.TemporaryPasswordGenerator;
import com.barbearia.domain.entities.Barber;
import com.barbearia.domain.entities.Products;
import com.barbearia.domain.entities.RolesEntity;
import com.barbearia.domain.entities.User;
import com.barbearia.infrastructure.persistence.BarberRepository;
import com.barbearia.infrastructure.persistence.ProductsRepository;
import com.barbearia.infrastructure.persistence.RolesRepository;
import com.barbearia.infrastructure.persistence.UserRepository;
import com.barbearia.shared.exceptions.EntityAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BarberService {

    private final BarberRepository barberRepository;
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final ProductsRepository productsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;

    @Transactional
    public BarberResponseDTO create (BarberResquestDTO dto){

        if (userRepository.existsByEmail(dto.email())) {
            throw new EntityAlreadyExistsException("User already exists");
        }

        RolesEntity barberRole = rolesRepository.findByName("ROLE_BARBER")
                .orElseGet(() -> rolesRepository.save(RolesEntity.builder()
                        .name("ROLE_BARBER")
                        .build()));

        String temporaryPassword = temporaryPasswordGenerator.generate();

        User user = userRepository.save(User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(temporaryPassword))
                .roles(Set.of(barberRole))
                .isActive(true)
                .mustChangePassword(true)
                .build());
        List<Products> products = productsRepository.findAllById(dto.productsId());

        Barber barber = Barber.builder()
                .name(dto.name())
                .phone(dto.phone())
                .user(user)
                .specialties(products)
                .build();
        return new BarberResponseDTO(barber,temporaryPassword);
    }
}
