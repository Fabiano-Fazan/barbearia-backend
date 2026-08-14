package com.barbearia.financial.application.service;

import com.barbearia.scheduling.domain.event.AppointmentCompletedEvent;
import com.barbearia.identity.application.security.AuthenticatedUserProvider;
import com.barbearia.staff.domain.model.Barber;
import com.barbearia.staff.application.service.BarberApplicationService;
import com.barbearia.shared.domain.exception.EntityAlreadyExistsException;
import com.barbearia.shared.domain.exception.ForbiddenOperationException;
import com.barbearia.shared.domain.exception.ResourceNotFoundException;
import com.barbearia.financial.application.dto.*;
import com.barbearia.financial.domain.model.*;
import com.barbearia.financial.infrastructure.persistence.CommissionRepository;
import com.barbearia.financial.infrastructure.persistence.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class FinancialApplicationService {
    private final TransactionRepository financialRepository;
    private final CommissionRepository commissionRepository;
    private final BarberApplicationService barberService;
    private final AuthenticatedUserProvider currentUser;

    @Transactional(readOnly = true)
    public List<BarberCommissionResponseDTO> getCommissionsByBarber(UUID barberId, CommissionStatus status) {
        if (currentUser.isBarber() && !currentUser.getCurrentBarberId().equals(barberId))
            throw new ForbiddenOperationException("You can only view your own commissions");
        return commissionRepository.findByBarberIdAndStatus(barberId, status).stream().map(BarberCommissionResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public FinancialSummaryDTO getFinancialSummary() {
        List<Transaction> transactions = financialRepository.findAll();
        BigDecimal incomes = sum(transactions, TransactionType.INCOME);
        BigDecimal expenses = sum(transactions, TransactionType.EXPENSE);
        BigDecimal pending = commissionRepository.findAll().stream()
                .filter(c -> c.getStatus() == CommissionStatus.PENDING)
                .map(Commission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long rendered = transactions.stream().filter(t -> t.getCategory() == TransactionCategory.SALE_SERVICE).count();
        return new FinancialSummaryDTO(incomes, expenses, incomes.subtract(expenses), pending, rendered);
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
        Barber barber = null;
        if (dto.barberId() != null) {
            barber = barberService.getBarberById(dto.barberId());
        }
        Transaction transaction = Transaction.builder()
                .description(dto.description())
                .amount(dto.amount())
                .type(dto.type())
                .category(dto.category())
                .paymentMethod(dto.paymentMethod())
                .barberId(dto.barberId())
                .barberName(barber != null ? barber.getName() : null)
                .appointmentId(dto.appointmentId())
                .build();
        return new TransactionResponseDTO(financialRepository.save(transaction));
    }

    @Transactional
    public List<BarberCommissionResponseDTO> payCommissions(PayCommissionRequestDTO dto) {
        var requestedIds = dto.commissionId().stream().distinct().toList();
        var commissions = commissionRepository.findAllById(requestedIds);
        if (commissions.size() != requestedIds.size() || commissions.isEmpty()) {
            throw new ResourceNotFoundException("Some commissions not found");
        }
        UUID barberId = commissions.getFirst().getBarberId();
        String barberName = commissions.getFirst().getBarberName();
        if (commissions.stream().anyMatch(c -> !barberId.equals(c.getBarberId())))
            throw new IllegalArgumentException("All commissions must belong to the same barber");
        commissions.forEach(Commission::pay);
        commissionRepository.saveAll(commissions);
        BigDecimal total = commissions.stream().map(Commission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        financialRepository.save(Transaction.builder()
                .description("Payment for commissions - Barber: " + barberName)
                .amount(total)
                .type(TransactionType.EXPENSE)
                .category(TransactionCategory.BARBER_COMMISSION)
                .paymentMethod(dto.paymentMethod())
                .barberId(barberId)
                .barberName(barberName)
                .build());
        return commissions.stream().map(BarberCommissionResponseDTO::new).toList();
    }

    @Transactional
    public void registerCompletedAppointment(AppointmentCompletedEvent event) {

        financialRepository.findByAppointmentId(event.appointmentId()).ifPresent(t -> {
            throw new EntityAlreadyExistsException("Financial transaction already exists for this appointment");
        });
        financialRepository.save(Transaction.builder()
                .description("Income from appointment #" + event.appointmentId())
                .amount(event.totalAmount()).type(TransactionType.INCOME).category(TransactionCategory.SALE_SERVICE)
                .paymentMethod(event.paymentMethod())
                .appointmentId(event.appointmentId())
                .barberId(event.barberId())
                .barberName(event.barberName())
                .build());

        BigDecimal amount = event.totalAmount().multiply(event.commissionRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        commissionRepository.save(Commission.builder()
                .barberId(event.barberId())
                .barberName(event.barberName())
                .appointmentId(event.appointmentId())
                .serviceAmount(event.totalAmount())
                .commissionRate(event.commissionRate())
                .commissionAmount(amount)
                .status(CommissionStatus.PENDING)
                .build());
    }

    private BigDecimal sum(List<Transaction> values, TransactionType type) {
        return values.stream().filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
