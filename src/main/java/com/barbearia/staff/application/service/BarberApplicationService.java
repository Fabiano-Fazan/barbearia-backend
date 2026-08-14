package com.barbearia.staff.application.service;

import com.barbearia.staff.application.dto.BarberRequestDTO;
import com.barbearia.staff.application.dto.BarberResponseDTO;
import com.barbearia.staff.application.dto.BarberMapper;
import com.barbearia.staff.domain.model.Barber;
import com.barbearia.staff.infrastructure.persistence.BarberRepository;
import com.barbearia.staff.infrastructure.persistence.BarberSpecifications;
import com.barbearia.identity.application.security.AuthenticatedUserProvider;
import com.barbearia.identity.domain.model.Role;
import com.barbearia.identity.application.service.RoleService;
import com.barbearia.identity.domain.model.User;
import com.barbearia.shared.domain.exception.EntityAlreadyExistsException;
import com.barbearia.shared.domain.exception.ResourceNotFoundException;
import com.barbearia.catalog.application.service.CatalogApplicationService;
import com.barbearia.identity.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarberApplicationService {

    private final BarberRepository barberRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final CatalogApplicationService productService;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final BarberMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void lockBarber(UUID barberId) {
        barberRepository.findByIdForUpdate(barberId)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
    }

    @Transactional(readOnly = true)
    public BarberResponseDTO getCurrentBarber() {
        Barber barber = getBarberById(authenticatedUserProvider.getCurrentBarberId());
        return mapper.toResponse(barber);
    }

    @Transactional(readOnly = true)
    public Barber getBarberById(UUID id) {
        return barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
    }

    @Transactional(readOnly = true)
    public Page<BarberResponseDTO> findBarbers(String name, String phone, UUID barberId, Pageable pageable){
        Specification<Barber> specification = Specification
                .where(BarberSpecifications.hasName(name))
                .and(BarberSpecifications.hasPhone(phone))
                .and(BarberSpecifications.hasId(barberId));
        return barberRepository.findAll(specification, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public BarberResponseDTO create (BarberRequestDTO dto){

        if (userService.existsByEmail(dto.email())) {
            throw new EntityAlreadyExistsException("User already exists");
        }

        Role barberRole = roleService.findByName("ROLE_BARBER");
        User user = userService.createUserByBarber(dto, barberRole);
        if (productService.getAllProductsById(dto.productsId()).size() != dto.productsId().stream().distinct().count())
            throw new ResourceNotFoundException("One or more products not found");

        Barber barber = Barber.builder()
                .name(dto.name())
                .phone(dto.phone())
                .userId(user.getId())
                .email(user.getEmail())
                .specialtyIds(dto.productsId())
                .commissionRate(dto.commissionRate())
                .build();
        return mapper.toResponse(barberRepository.save(barber));
    }

    @Transactional
    public BarberResponseDTO  update (UUID id, BarberRequestDTO dto){
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        processData(barber, dto);
        barber.setCommissionRate(dto.commissionRate());
        return mapper.toResponse(barberRepository.save(barber));
    }

    @Transactional
    public BarberResponseDTO  updateCurrentBarber(BarberRequestDTO dto){
        Barber barber = getBarberById(authenticatedUserProvider.getCurrentBarberId());
        processData(barber, dto);
        return mapper.toResponse(barberRepository.save(barber));
    }

    @Transactional
    public void deleteCurrentBarber(){
        Barber barber = getBarberById(authenticatedUserProvider.getCurrentBarberId());
        UUID userId = barber.getUserId();
        barberRepository.delete(barber);
        userService.deleteUser(userId);
    }

    @Transactional
    public void delete(UUID id){
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        UUID userId = barber.getUserId();
        barberRepository.delete(barber);
        userService.deleteUser(userId);
    }

    private void processData(Barber barber, BarberRequestDTO dto) {
        barber.setName(dto.name());
        barber.setPhone(dto.phone());
        if (productService.getAllProductsById(dto.productsId()).size() != dto.productsId().stream().distinct().count())
            throw new ResourceNotFoundException("One or more products not found");
        barber.setSpecialtyIds(dto.productsId());
    }
}
