package com.barbearia.barber;

import com.barbearia.barber.dto.BarberRequestDTO;
import com.barbearia.barber.dto.BarberResponseDTO;
import com.barbearia.auth.AuthenticatedUserProvider;
import com.barbearia.auth.TemporaryPasswordGenerator;
import com.barbearia.product.Product;
import com.barbearia.auth.Role;
import com.barbearia.auth.User;
import com.barbearia.product.ProductRepository;
import com.barbearia.auth.RoleRepository;
import com.barbearia.auth.UserRepository;
import com.barbearia.core.exceptions.EntityAlreadyExistsException;
import com.barbearia.core.exceptions.ResourceNotFoundException;
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
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional(readOnly = true)
    public BarberResponseDTO getCurrentBarber() {
        Barber barber = authenticatedUserProvider.getCurrentBarber();
        return new BarberResponseDTO(barber);
    }

    @Transactional(readOnly = true)
    public Page<BarberResponseDTO> findBarbers(String name, String phone, UUID barberId, Pageable pageable){
        Specification<Barber> specification = Specification
                .where(BarberSpecifications.hasName(name))
                .and(BarberSpecifications.hasPhone(phone))
                .and(BarberSpecifications.hasId(barberId));
        return barberRepository.findAll(specification, pageable)
                .map(BarberResponseDTO::new);
    }

    @Transactional
    public BarberResponseDTO create (BarberRequestDTO dto){

        if (userRepository.existsByEmail(dto.email())) {
            throw new EntityAlreadyExistsException("User already exists");
        }

        Role barberRole = roleRepository.findByName("ROLE_BARBER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("ROLE_BARBER")
                        .build()));

        String temporaryPassword = temporaryPasswordGenerator.generate();

        User user = userRepository.save(User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(temporaryPassword))
                .roles(Set.of(barberRole))
                .mustChangePassword(true)
                .build());
        List<Product> products = productRepository.findAllById(dto.productsId());

        Barber barber = Barber.builder()
                .name(dto.name())
                .phone(dto.phone())
                .user(user)
                .specialties(products)
                .commissionRate(dto.commissionRate())
                .build();
        return new BarberResponseDTO(barberRepository.save(barber), temporaryPassword);
    }

    @Transactional
    public BarberResponseDTO  update (UUID id, BarberRequestDTO dto){
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        processData(barber, dto);
        barber.setCommissionRate(dto.commissionRate());
        return new BarberResponseDTO(barberRepository.save(barber), null);
    }

    @Transactional
    public BarberResponseDTO  updateCurrentBarber(BarberRequestDTO dto){
        Barber barber = authenticatedUserProvider.getCurrentBarber();
        processData(barber, dto);
        return new BarberResponseDTO(barberRepository.save(barber), null);
    }

    @Transactional
    public void deleteCurrentBarber(){
        Barber barber = authenticatedUserProvider.getCurrentBarber();
        User user = barber.getUser();
        barberRepository.delete(barber);
        userRepository.delete(user);
    }

    @Transactional
    public void delete(UUID id){
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        User user = barber.getUser();
        barberRepository.delete(barber);
        userRepository.delete(user);
    }

    private void processData(Barber barber, BarberRequestDTO dto) {
        barber.setName(dto.name());
        barber.setPhone(dto.phone());
        barber.setSpecialties(productRepository.findAllById(dto.productsId()));
    }
}
