package com.barbearia.user;

import com.barbearia.auth.TemporaryPasswordGenerator;
import com.barbearia.auth.dto.ChangePasswordRequestDTO;
import com.barbearia.auth.dto.RegisterRequestDTO;
import com.barbearia.barber.dto.BarberRequestDTO;
import com.barbearia.core.exceptions.ResourceNotFoundException;
import com.barbearia.role.Role;
import lombok.RequiredArgsConstructor;
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

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User existsByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
    public void createUserByBarber(BarberRequestDTO dto, Role roles) {
        String temporaryPassword = temporaryPasswordGenerator.generate();
        userRepository.save(User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(temporaryPassword))
                .roles(Set.of(roles))
                .mustChangePassword(true)
                .build());
    }

    @Transactional
    public User createUserByClient(String name, String email, Role roles) {
        return userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode("defaultPassword"))
                .roles(Set.of(roles))
                .build());
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
