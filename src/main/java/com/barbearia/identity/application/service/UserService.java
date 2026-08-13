package com.barbearia.identity.application.service;

import com.barbearia.identity.infrastructure.security.TemporaryPasswordGenerator;
import com.barbearia.identity.application.dto.ChangePasswordRequestDTO;
import com.barbearia.identity.application.dto.RegisterRequestDTO;
import com.barbearia.staff.application.dto.BarberRequestDTO;
import com.barbearia.shared.domain.exception.ResourceNotFoundException;
import com.barbearia.identity.domain.model.Role;
import com.barbearia.identity.domain.model.User;
import com.barbearia.identity.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;

    @Value("${CLIENT_PASSWORD}")
    private String clientPassword;

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void createUserByAdmin(RegisterRequestDTO dto, Role roles) {
        userRepository.save(User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .roles(Set.of(roles))
                .build());
    }

    @Transactional
    public User createUserByBarber(BarberRequestDTO dto, Role roles) {
        String temporaryPassword = temporaryPasswordGenerator.generate();
        return userRepository.save(User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(temporaryPassword))
                .roles(Set.of(roles))
                .mustChangePassword(true)
                .build());
    }

    @Transactional
    public User createUserByClient(String name, String email, Role roles) {
        var existing = userRepository.findByEmail(email);
        return existing.orElseGet(() -> userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(clientPassword))
                .roles(Set.of(roles))
                .build()));
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = this.getUserById(id);
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequestDTO dto) {
        User user = this.getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }
}
