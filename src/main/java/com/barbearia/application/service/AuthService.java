package com.barbearia.application.service;

import com.barbearia.application.dto.request.ChangePasswordRequestDTO;
import com.barbearia.application.dto.request.LoginRequestDTO;
import com.barbearia.application.dto.request.RegisterRequestDTO;
import com.barbearia.application.dto.response.TokenResponseDTO;
import com.barbearia.domain.entities.RolesEntity;
import com.barbearia.domain.entities.User;
import com.barbearia.infrastructure.configuration.TokenProvider;
import com.barbearia.infrastructure.persistence.RolesRepository;
import com.barbearia.infrastructure.persistence.UserRepository;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Transactional
    public void registerAdmin(RegisterRequestDTO dto) throws BadRequestException {

        if(userRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("User already exists");
        }

        RolesEntity roles = rolesRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> rolesRepository.save(RolesEntity.builder()
                        .name("ROLE_ADMIN")
                        .build()));

        userRepository.save(User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .roles(Set.of(roles))
                .isActive(true)
                .build());
    }

    public TokenResponseDTO login(LoginRequestDTO dto) throws BadCredentialsException {
        try{
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.email(),
                            dto.password()
                    ));
            User user = (User) authenticate.getPrincipal();

            if (user == null){
                throw new BadCredentialsException("Invalid credentials");
            }
            String token = tokenProvider.getToken(authenticate);

            return new TokenResponseDTO(token, user.isMustChangePassword());

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }
}
