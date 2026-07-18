package com.barbearia.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final ClientService clientService;
    private final ProductService productService;
}
