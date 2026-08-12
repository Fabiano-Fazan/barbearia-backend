package com.barbearia.barber;

import com.barbearia.barber.dto.BarberRequestDTO;
import com.barbearia.barber.dto.BarberResponseDTO;
import com.barbearia.auth.AuthenticatedUserProvider;
import com.barbearia.product.Product;
import com.barbearia.role.Role;
import com.barbearia.role.RoleService;
import com.barbearia.user.User;
import com.barbearia.core.exceptions.EntityAlreadyExistsException;
import com.barbearia.core.exceptions.ResourceNotFoundException;
import com.barbearia.product.ProductService;
import com.barbearia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarberService {

    private final BarberRepository barberRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final ProductService productService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional(readOnly = true)
    public BarberResponseDTO getCurrentBarber() {
        Barber barber = authenticatedUserProvider.getCurrentBarber();
        return new BarberResponseDTO(barber);
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
                .map(BarberResponseDTO::new);
    }

    @Transactional
    public BarberResponseDTO create (BarberRequestDTO dto){

        if (userService.existsByEmail(dto.email()) != null) {
            throw new EntityAlreadyExistsException("User already exists");
        }

        Role barberRole = roleService.findByName("ROLE_BARBER");
        userService.createUserByBarber(dto, barberRole);

        List<Product> products = productService.getAllProductsById(dto.productsId());

        Barber barber = Barber.builder()
                .name(dto.name())
                .phone(dto.phone())
                .user(userService.getUserByEmail(dto.email()))
                .specialties(products)
                .commissionRate(dto.commissionRate())
                .build();
        return new BarberResponseDTO(barberRepository.save(barber), barber.getUser().getPassword());
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
        userService.deleteUser(user.getId());
    }

    @Transactional
    public void delete(UUID id){
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        User user = barber.getUser();
        barberRepository.delete(barber);
        userService.deleteUser(user.getId());
    }

    private void processData(Barber barber, BarberRequestDTO dto) {
        barber.setName(dto.name());
        barber.setPhone(dto.phone());
        barber.setSpecialties(productService.getAllProductsById(dto.productsId()));
    }
}
