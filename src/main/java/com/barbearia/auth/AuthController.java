package com.barbearia.auth;

import com.barbearia.auth.dto.LoginRequestDTO;
import com.barbearia.auth.dto.RegisterRequestDTO;
import com.barbearia.auth.dto.TokenResponseDTO;
import com.barbearia.core.exceptions.EntityAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register-admin")
    public ResponseEntity<Void> registerAdmin(@RequestBody @Valid RegisterRequestDTO dto) throws EntityAlreadyExistsException {
        authService.registerAdmin(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login-admin")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) throws BadCredentialsException {
        TokenResponseDTO token = authService.login(dto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
