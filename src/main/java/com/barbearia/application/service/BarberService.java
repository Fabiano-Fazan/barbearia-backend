package com.barbearia.application.service;

import com.barbearia.application.dto.request.BarberResquestDTO;
import com.barbearia.application.dto.response.BarberResponseDTO;
import com.barbearia.application.util.AuthenticatedUserProvider;
import com.barbearia.application.util.TemporaryPasswordGenerator;
import com.barbearia.domain.entities.Barber;
import com.barbearia.domain.entities.Products;
import com.barbearia.domain.entities.RolesEntity;
import com.barbearia.domain.entities.User;
import com.barbearia.infrastructure.persistence.BarberRepository;
import com.barbearia.infrastructure.persistence.ProductsRepository;
import com.barbearia.infrastructure.persistence.RolesRepository;
import com.barbearia.infrastructure.persistence.UserRepository;
import com.barbearia.infrastructure.persistence.specifications.BarberSpecifications;
import com.barbearia.shared.exceptions.EntityAlreadyExistsException;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarberService {

    private final BarberRepository barberRepository;
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final ProductsRepository productsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public BarberResponseDTO getCurrentBarber() {
        Barber barber = authenticatedUserProvider.getCurrentBarber();
        return new BarberResponseDTO(barber);
    }

    public Page<BarberResponseDTO> findBarbers(String name, String phone, UUID barberId, Pageable pageable){
        Specification<Barber> specification = Specification
                .where(BarberSpecifications.hasName(name))
                .and(BarberSpecifications.hasPhone(phone))
                .and(BarberSpecifications.hasId(barberId));
        return barberRepository.findAll(specification, pageable)
                .map(BarberResponseDTO::new);
    }

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
        return new BarberResponseDTO(barberRepository.save(barber), temporaryPassword);
    }

    @Transactional
    public BarberResponseDTO  update (UUID id, BarberResquestDTO dto){
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        processData(barber, dto);
        return new BarberResponseDTO(barberRepository.save(barber), null);
    }

    @Transactional
    public BarberResponseDTO  updateCurrentBarber(BarberResquestDTO dto){
        Barber barber = authenticatedUserProvider.getCurrentBarber();
        processData(barber, dto);
        return new BarberResponseDTO(barberRepository.save(barber), null);
    }

    @Transactional
    public void deleteCurrentBarber(){
        Barber barber = authenticatedUserProvider.getCurrentBarber();
        User user = barber.getUser();
        barber.setIsActive(false);
        user.setIsActive(false);
        barberRepository.save(barber);
        userRepository.save(user);
    }

    @Transactional
    public void delete(UUID id){
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        User user = barber.getUser();
        barber.setIsActive(false);
        user.setIsActive(false);
        barberRepository.save(barber);
        userRepository.save(user);
    }

    private void processData(Barber barber,BarberResquestDTO dto) {
        barber.setName(dto.name());
        barber.setPhone(dto.phone());
        barber.setSpecialties(productsRepository.findAllById(dto.productsId()));
    }
}
