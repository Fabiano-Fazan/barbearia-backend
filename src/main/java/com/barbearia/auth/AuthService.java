package com.barbearia.auth;

import com.barbearia.auth.dto.ChangePasswordRequestDTO;
import com.barbearia.auth.dto.LoginRequestDTO;
import com.barbearia.auth.dto.RegisterRequestDTO;
import com.barbearia.auth.dto.TokenResponseDTO;
import com.barbearia.core.exceptions.ResourceNotFoundException;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Transactional
    public void registerAdmin(RegisterRequestDTO dto) throws BadRequestException {

        if(userRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("User already exists");
        }

        Role roles = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("ROLE_ADMIN")
                        .build()));

        userRepository.save(User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .roles(Set.of(roles))
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
