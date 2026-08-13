package com.barbearia.financial.infrastructure.web;


import com.barbearia.financial.application.dto.*;
import com.barbearia.financial.application.service.FinancialApplicationService;
import com.barbearia.financial.domain.model.CommissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/financial")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialApplicationService financialService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER') ")
    @GetMapping("/commissions")
    public ResponseEntity<List<BarberCommissionResponseDTO>> getCommissions(
            @RequestParam(required = false) UUID barberId,
            @RequestParam(required = false) CommissionStatus status
    ) {
        List<BarberCommissionResponseDTO> commissions = financialService.getCommissionsByBarber(barberId, status);
        return new ResponseEntity<>(commissions, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') ")
    @GetMapping("/summary")
    public ResponseEntity<FinancialSummaryDTO> getFinancialSummary() {
        FinancialSummaryDTO summary = financialService.getFinancialSummary();
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') ")
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionRequestDTO requestDTO) {
        TransactionResponseDTO transaction = financialService.createTransaction(requestDTO);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') ")
    @PutMapping
    public ResponseEntity<List<BarberCommissionResponseDTO>> payCommissions(@RequestBody PayCommissionRequestDTO dto) {
        List<BarberCommissionResponseDTO> commissions = financialService.payCommissions(dto);
        return new ResponseEntity<>(commissions, HttpStatus.OK);
    }

}
